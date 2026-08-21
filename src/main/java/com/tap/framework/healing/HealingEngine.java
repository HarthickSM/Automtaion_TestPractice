package com.tap.framework.healing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Scores the elements of the live DOM against the stored fingerprint of a locator and returns the
 * best match. Only matches at or above {@code self.healing.min.score} are accepted, so a broken
 * locator still fails instead of silently driving the wrong element.
 */
public final class HealingEngine {

    private static final Logger LOG = LogManager.getLogger(HealingEngine.class);

    private static final double ID_WEIGHT = 0.30;
    private static final double NAME_WEIGHT = 0.20;
    private static final double TEXT_WEIGHT = 0.15;
    private static final double CLASS_WEIGHT = 0.15;
    private static final double TYPE_WEIGHT = 0.10;
    private static final double PLACEHOLDER_WEIGHT = 0.10;

    private HealingEngine() {
    }

    public static Optional<WebElement> heal(SearchContext context, String locatorName,
            ElementSnapshot snapshot, double minimumScore) {
        List<WebElement> candidates = context.findElements(
                By.tagName(snapshot.getTag().isEmpty() ? "*" : snapshot.getTag()));
        return candidates.stream()
                .map(candidate -> new ScoredElement(candidate, score(snapshot, candidate)))
                .filter(scored -> scored.score >= minimumScore)
                .max(Comparator.comparingDouble(scored -> scored.score))
                .map(scored -> {
                    String message = String.format(
                            "Healed '%s' with a %.0f%% match on %s", locatorName, scored.score * 100,
                            ElementSnapshot.of(scored.element));
                    LOG.warn(message);
                    HealingLog.record(message);
                    return scored.element;
                });
    }

    /**
     * Weighted average over the attributes the snapshot actually captured, so an element that only
     * ever had an {@code id} is still scored out of 1 rather than out of the full weight table.
     */
    private static double score(ElementSnapshot snapshot, WebElement candidate) {
        try {
            ElementSnapshot live = ElementSnapshot.of(candidate);
            double matched = 0;
            double available = 0;
            double[][] pairs = {
                    {ID_WEIGHT, similarity(snapshot.getId(), live.getId()), weigh(snapshot.getId())},
                    {NAME_WEIGHT, similarity(snapshot.getName(), live.getName()), weigh(snapshot.getName())},
                    {TEXT_WEIGHT, similarity(snapshot.getText(), live.getText()), weigh(snapshot.getText())},
                    {CLASS_WEIGHT, similarity(snapshot.getClasses(), live.getClasses()),
                            weigh(snapshot.getClasses())},
                    {TYPE_WEIGHT, similarity(snapshot.getType(), live.getType()), weigh(snapshot.getType())},
                    {PLACEHOLDER_WEIGHT, similarity(snapshot.getPlaceholder(), live.getPlaceholder()),
                            weigh(snapshot.getPlaceholder())}};
            for (double[] pair : pairs) {
                available += pair[0] * pair[2];
                matched += pair[0] * pair[1];
            }
            return available == 0 ? 0 : matched / available;
        } catch (WebDriverException e) {
            return 0;
        }
    }

    private static double weigh(String snapshotValue) {
        return snapshotValue.isBlank() ? 0 : 1;
    }

    /** 1 for an exact match, a token overlap ratio otherwise, 0 when either side is blank. */
    private static double similarity(String expected, String actual) {
        if (expected.isBlank() || actual.isBlank()) {
            return 0;
        }
        if (expected.equalsIgnoreCase(actual)) {
            return 1;
        }
        List<String> expectedTokens = List.of(expected.toLowerCase().split("[\\s_-]+"));
        List<String> actualTokens = List.of(actual.toLowerCase().split("[\\s_-]+"));
        long shared = expectedTokens.stream().filter(actualTokens::contains).count();
        return (double) shared / Math.max(expectedTokens.size(), actualTokens.size());
    }

    private static final class ScoredElement {

        private final WebElement element;
        private final double score;

        private ScoredElement(WebElement element, double score) {
            this.element = element;
            this.score = score;
        }
    }
}

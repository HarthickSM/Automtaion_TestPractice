package com.tap.framework.healing;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * Self healing locator. It behaves like any other {@link By} - so it works with every wait,
 * {@code Select} and page object in the framework - but resolves in three stages:
 *
 * <pre>
 * primary locator            -> snapshot the element for later
 *   |- empty                 -> declared fallback locators, in order
 *        |- empty            -> similarity match against the stored snapshot (HealingEngine)
 * </pre>
 *
 * Every repair is logged and attached to the Extent report, so a healed run is visibly different
 * from a clean one.
 */
public final class SmartBy extends By {

    private static final Logger LOG = LogManager.getLogger(SmartBy.class);

    private final String name;
    private final By primary;
    private final List<By> fallbacks;

    private SmartBy(String name, By primary, List<By> fallbacks) {
        this.name = name;
        this.primary = primary;
        this.fallbacks = fallbacks;
    }

    public static SmartBy of(String name, By primary, By... fallbacks) {
        return new SmartBy(name, primary, List.of(fallbacks));
    }

    public String name() {
        return name;
    }

    public By primary() {
        return primary;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> found = context.findElements(primary);
        if (!found.isEmpty()) {
            remember(found.get(0));
            return found;
        }
        if (!ConfigReader.getBoolean(ConfigKey.SELF_HEALING)) {
            return found;
        }
        for (By fallback : fallbacks) {
            List<WebElement> healed = context.findElements(fallback);
            if (!healed.isEmpty()) {
                String message = String.format("Healed '%s': %s failed, matched fallback %s", name,
                        primary, fallback);
                LOG.warn(message);
                HealingLog.record(message);
                remember(healed.get(0));
                return healed;
            }
        }
        return LocatorRepository.get(name)
                .flatMap(snapshot -> HealingEngine.heal(context, name, snapshot,
                        Double.parseDouble(ConfigReader.get(ConfigKey.SELF_HEALING_MIN_SCORE))))
                .map(List::of)
                .orElseGet(List::of);
    }

    private void remember(WebElement element) {
        try {
            ElementSnapshot snapshot = ElementSnapshot.of(element);
            Optional<ElementSnapshot> stored = LocatorRepository.get(name);
            if (stored.isEmpty() || !stored.get().equals(snapshot)) {
                LocatorRepository.put(name, snapshot);
            }
        } catch (RuntimeException e) {
            LOG.debug("Unable to snapshot {}", name, e);
        }
    }

    @Override
    public String toString() {
        return "SmartBy(" + name + " -> " + primary
                + (fallbacks.isEmpty() ? "" : ", fallbacks " + Arrays.toString(fallbacks.toArray()))
                + ")";
    }
}

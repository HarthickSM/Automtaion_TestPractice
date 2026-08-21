package com.tap.framework.healing;

import java.util.ArrayList;
import java.util.List;

/**
 * Per test record of the locators that had to be healed, so the report shows which locators are
 * drifting instead of hiding the repair.
 */
public final class HealingLog {

    private static final ThreadLocal<List<String>> EVENTS = ThreadLocal.withInitial(ArrayList::new);

    private HealingLog() {
    }

    public static void record(String message) {
        EVENTS.get().add(message);
    }

    public static List<String> events() {
        return List.copyOf(EVENTS.get());
    }

    public static void clear() {
        EVENTS.remove();
    }
}

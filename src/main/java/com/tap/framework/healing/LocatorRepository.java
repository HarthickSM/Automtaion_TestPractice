package com.tap.framework.healing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.exceptions.FrameworkException;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stores the last known good {@link ElementSnapshot} per locator name in
 * {@code test-output/healing/locator-repository.json}, so healing also works on the first run of a
 * later session (the file survives between runs).
 */
public final class LocatorRepository {

    private static final Logger LOG = LogManager.getLogger(LocatorRepository.class);
    private static final ObjectMapper MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final File FILE =
            new File(FrameworkConstants.HEALING_DIR, "locator-repository.json");
    private static final Map<String, ElementSnapshot> SNAPSHOTS = load();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(LocatorRepository::save));
    }

    private LocatorRepository() {
    }

    public static Optional<ElementSnapshot> get(String locatorName) {
        return Optional.ofNullable(SNAPSHOTS.get(locatorName));
    }

    public static void put(String locatorName, ElementSnapshot snapshot) {
        SNAPSHOTS.put(locatorName, snapshot);
    }

    public static void save() {
        try {
            File directory = FILE.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                throw new FrameworkException("Unable to create " + directory.getAbsolutePath());
            }
            MAPPER.writeValue(FILE, SNAPSHOTS);
        } catch (Exception e) {
            LOG.warn("Unable to persist the locator repository", e);
        }
    }

    private static Map<String, ElementSnapshot> load() {
        if (!FILE.exists()) {
            return new ConcurrentHashMap<>();
        }
        try {
            Map<String, ElementSnapshot> stored =
                    MAPPER.readValue(FILE, new TypeReference<Map<String, ElementSnapshot>>() {
                    });
            return new ConcurrentHashMap<>(stored);
        } catch (Exception e) {
            LOG.warn("Unable to read {}, starting with an empty locator repository", FILE, e);
            return new ConcurrentHashMap<>();
        }
    }
}

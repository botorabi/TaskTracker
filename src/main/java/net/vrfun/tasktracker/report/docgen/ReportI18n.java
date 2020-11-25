/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

package net.vrfun.tasktracker.report.docgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.*;

public class ReportI18n {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public enum Locale {
        EN,
        DE
    }

    private final Map<String, String> translations = new HashMap<>();

    static public ReportI18n build(@NonNull final Locale locale) throws Exception {
        ReportI18n reportI18n = new ReportI18n();
        reportI18n.setup(locale);
        return reportI18n;
    }

    private ReportI18n() {}

    @Nullable
    public final String translate(@NonNull final String token) {
        if (translations.containsKey(token)) {
            return translations.get(token);
        }
        LOGGER.warn("Localization token '{}' not found!", token);
        return null;
    }

    protected void setup(@NonNull final Locale locale) throws Exception {
        switch(locale) {
            case EN:
                loadLocalizationFile("doc-template/messages.properties");
                break;
            case DE:
                loadLocalizationFile("doc-template/messages_de.properties");
                break;
        }
    }

    protected void loadLocalizationFile(@NonNull final String resourceFileName) throws IOException {
        Resource resource = new ClassPathResource(resourceFileName);
        Properties loadedProperties = PropertiesLoaderUtils.loadProperties(resource);
        loadedProperties.forEach((key, value) -> translations.put(key.toString(), value.toString()));
        LOGGER.debug("Localization file '{}' successfully loaded", resourceFileName);
    }
}

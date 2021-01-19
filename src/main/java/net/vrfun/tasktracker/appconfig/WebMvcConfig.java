/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.appconfig;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;


/**
 * Configure the Web MVC
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

    /**
     * Is development mode enabled?
     */
    @Value("${enable-dev-mode: false}")
    private boolean developmentModeEnabled;

    /**
     * Pass -Duse-filesystem-resources=true on java command line in order to use filesystem web resources.
     */
    @Value("${use-filesystem-resources: false}")
    private boolean useFileSystemResources;

    /**
     * During development we set the resource handler to point to the filesystem instead of pointing to the
     * packaged resource folder. This way we can omit repackaging on every Web source change.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LOGGER.debug("enable-dev-mode: {}, use-filesystem-resources: {}",
                (developmentModeEnabled ? "YES": "NO"), (useFileSystemResources ? "YES": "NO"));

        if (developmentModeEnabled && useFileSystemResources) {
            File file = new File("");
            String resourceFolder = "file:" + file.getAbsolutePath() + "/src/main/resources/static/";
            LOGGER.info("dev run: using filesystem resource folder: {}", resourceFolder);

            registry
                    .addResourceHandler("/**")
                    .addResourceLocations(resourceFolder);
        }
    }
}

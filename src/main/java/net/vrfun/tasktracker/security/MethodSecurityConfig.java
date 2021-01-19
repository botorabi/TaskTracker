/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.*;

/**
 * Method security configuration.
 *
 * Feel free to extend the features by for instance:
 *
 *  prePostEnabled = true,
 *  jsr250Enabled = true)
 *
 * @author          boto
 * Creation Date    July 2020
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
}

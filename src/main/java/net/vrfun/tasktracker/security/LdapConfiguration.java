/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.security;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.*;
import org.springframework.stereotype.Component;


@Component
public class LdapConfiguration {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${tasktracker.ldap.urls: ''}")
    private String LDAP_URL;

    @Value("${tasktracker.ldap.mgm.dn: ''}")
    private String LDAP_MGM_DN;

    @Value("${tasktracker.ldap.mgm.pw: ''}")
    private String LDAP_MGM_PW;

    @Value("${tasktracker.ldap.grp.filter: ''}")
    private String LDAP_GRP_FILTER;

    @Value("${tasktracker.ldap.user.dn.pattern: ''}")
    private String USER_DN_PATTERN;

    private BindAuthenticator bindAuthenticator;


    public DirContextOperations authenticate(@NonNull Authentication authentication) {
        return bindAuthenticator.authenticate(authentication);
    }

    public void setup() {
        try {
            DefaultSpringSecurityContextSource contextSource = buildContextSource();
            contextSource.setCacheEnvironmentProperties(false);

            LdapUserSearch userSearch = new FilterBasedLdapUserSearch("", LDAP_GRP_FILTER, contextSource);

            bindAuthenticator = new BindAuthenticator(contextSource);
            bindAuthenticator.setUserSearch(userSearch);
            String[] patterns = {USER_DN_PATTERN};
            bindAuthenticator.setUserDnPatterns(patterns);
        }
        catch(Throwable throwable) {
            LOGGER.warn("Could not setup LDAP authentication, reason: {}", throwable.getMessage());
        }
    }

    private DefaultSpringSecurityContextSource buildContextSource() {
        LOGGER.info("Using LDAP URL: {}", LDAP_URL);
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(LDAP_URL);
        contextSource.setUserDn(LDAP_MGM_DN);
        contextSource.setPassword(LDAP_MGM_PW);
        return contextSource;
    }
}

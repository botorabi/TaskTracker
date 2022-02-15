/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.user;

import net.vrfun.tasktracker.common.BaseEntity;
import org.springframework.lang.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

@Entity
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String realName;

    @Pattern(regexp = "^[A-Za-z0-9- ]{1,30}$")
    @Column(unique=true, nullable=false)
    private String login;

    private String ldapLogin;

    @Column(nullable=false)
    private String password;

    private String email;

    private Instant dateCreation;

    private Instant lastLogin;

    @ManyToMany(targetEntity = UserRole.class, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<UserRole> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(@NonNull final String realName) {
        this.realName = realName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(@NonNull final String login) {
        this.login = login;
    }

    public String getLdapLogin() {
        return ldapLogin;
    }

    public void setLdapLogin(@NonNull final String ldapLogin) {
        this.ldapLogin = ldapLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull final String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull final String email) {
        this.email = email;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(@Nullable Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(@Nullable Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Collection<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }
}

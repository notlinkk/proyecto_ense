package com.mentory.ense_proyect.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
    @Id
    private String rolename;

    private Set<Role> includes;

    private Set<Permission> permissions;

    public Role() {}

    public String getRolename() {
        return rolename;
    }

    public Role setRolename(String rolename) {
        this.rolename = rolename;
        return this;
    }

    public Set<Role> getIncludes() {
        return includes;
    }

    public Role setIncludes(Set<Role> includes) {
        this.includes = includes;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Role setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }
}

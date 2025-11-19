package com.mentory.ense_proyect.model;

import java.util.Set;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;



@Entity
@Table(name = "roles")

public class Role {
    @Id
    private String rolename;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_hierarchy",
        joinColumns = @JoinColumn(name = "role"),
        inverseJoinColumns = @JoinColumn(name = "included_role")
    )
    private Set<Role> includes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role"),
        inverseJoinColumns = @JoinColumn(name = "permission")
    )
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

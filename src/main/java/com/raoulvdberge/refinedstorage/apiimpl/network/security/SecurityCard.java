package com.raoulvdberge.refinedstorage.apiimpl.network.security;

import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCard;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecurityCard implements ISecurityCard {
    private UUID bound;
    private Map<Permission, Boolean> permissions = new HashMap<>();

    public SecurityCard(UUID bound) {
        this.bound = bound;
    }

    public Map<Permission, Boolean> getPermissions() {
        return permissions;
    }

    @Override
    public UUID getBound() {
        return bound;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return permissions.get(permission);
    }
}

package com.refinedmods.refinedstorage.apiimpl.network.security;

import com.refinedmods.refinedstorage.api.network.security.ISecurityCard;
import com.refinedmods.refinedstorage.api.network.security.Permission;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecurityCard implements ISecurityCard {
    private final UUID owner;
    private final Map<Permission, Boolean> permissions = new HashMap<>();

    public SecurityCard(@Nullable UUID owner) {
        this.owner = owner;
    }

    public Map<Permission, Boolean> getPermissions() {
        return permissions;
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return permissions.get(permission);
    }
}

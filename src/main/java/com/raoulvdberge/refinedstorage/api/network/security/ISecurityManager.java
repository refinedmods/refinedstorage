package com.raoulvdberge.refinedstorage.api.network.security;

import net.minecraft.entity.player.EntityPlayer;

/**
 * The security manager of a network.
 */
public interface ISecurityManager {
    /**
     * @param permission the permission to check for
     * @param player     the player to check that permission for
     * @return whether the player has the given permission
     */
    boolean hasPermission(Permission permission, EntityPlayer player);

    /**
     * Rebuilds the security list.
     */
    void rebuild();
}

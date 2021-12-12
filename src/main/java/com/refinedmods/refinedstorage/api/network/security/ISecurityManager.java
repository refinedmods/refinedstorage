package com.refinedmods.refinedstorage.api.network.security;

import net.minecraft.world.entity.player.Player;

/**
 * The security manager of a network.
 */
public interface ISecurityManager {
    /**
     * @param permission the permission to check for
     * @param player     the player to check that permission for
     * @return true if the player has the given permission, false otherwise
     */
    boolean hasPermission(Permission permission, Player player);

    /**
     * Invalidates the security list.
     */
    void invalidate();
}

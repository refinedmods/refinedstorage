package com.refinedmods.refinedstorage.api.network.security

import net.minecraft.entity.player.PlayerEntity


/**
 * The security manager of a network.
 */
interface ISecurityManager {
    /**
     * @param permission the permission to check for
     * @param player     the player to check that permission for
     * @return true if the player has the given permission, false otherwise
     */
    fun hasPermission(permission: Permission?, player: PlayerEntity?): Boolean

    /**
     * Invalidates the security list.
     */
    fun invalidate()
}
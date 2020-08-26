package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResult
import net.minecraft.util.Rotation

class WrenchItem : Item(Properties().group(RS.MAIN_GROUP).maxStackSize(1)) {
    fun onItemUseFirst(stack: ItemStack?, ctx: ItemUseContext): ActionResult {
        if (ctx.getWorld().isRemote) {
            return ActionResult.CONSUME
        }
        val network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(ctx.getWorld().getBlockEntity(ctx.getPos())))
        if (network != null && !network.securityManager!!.hasPermission(Permission.BUILD, ctx.getPlayer())) {
            WorldUtils.sendNoPermissionMessage(ctx.getPlayer())
            return ActionResult.FAIL
        }
        val state: BlockState = ctx.getWorld().getBlockState(ctx.getPos())
        ctx.getWorld().setBlockState(ctx.getPos(), state.rotate(Rotation.CLOCKWISE_90))
        return ActionResult.CONSUME
    }

    init {
        this.setRegistryName(RS.ID, "wrench")
    }
}
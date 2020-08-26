package com.refinedmods.refinedstorage.apiimpl.network.grid.factory

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory
import com.refinedmods.refinedstorage.tile.grid.WirelessGrid
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class WirelessGridGridFactory : IGridFactory {
    @Nullable
    override fun createFromStack(player: PlayerEntity?, stack: ItemStack?, slotId: Int): IGrid? {
        return WirelessGrid(stack, player!!.server, slotId)
    }

    @Nullable
    override fun createFromBlock(player: PlayerEntity?, pos: BlockPos?): IGrid? {
        return null
    }

    @Nullable
    override fun getRelevantTile(world: World?, pos: BlockPos?): BlockEntity? {
        return null
    }

    override val type: GridFactoryType
        get() = GridFactoryType.STACK

    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "wireless_grid")
    }
}
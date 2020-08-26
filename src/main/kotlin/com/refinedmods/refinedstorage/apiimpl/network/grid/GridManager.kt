package com.refinedmods.refinedstorage.apiimpl.network.grid

import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory
import com.refinedmods.refinedstorage.api.network.grid.IGridManager
import com.refinedmods.refinedstorage.container.factory.GridContainerProvider
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.network.NetworkHooks
import org.apache.commons.lang3.tuple.Pair
import java.util.*


class GridManager : IGridManager {
    private val factories: MutableMap<Identifier?, IGridFactory?> = HashMap<Identifier?, IGridFactory?>()
    override fun add(id: Identifier?, factory: IGridFactory?) {
        factories[id] = factory
    }

    override fun openGrid(id: Identifier?, player: ServerPlayerEntity?, pos: BlockPos?) {
        openGrid(id, player, null, pos, -1)
    }

    override fun openGrid(id: Identifier?, player: ServerPlayerEntity?, stack: ItemStack?, slotId: Int) {
        openGrid(id, player, stack, null, slotId)
    }

    private fun openGrid(id: Identifier?, player: ServerPlayerEntity?, @Nullable stack: ItemStack?, @Nullable pos: BlockPos?, slotId: Int) {
        val grid: Pair<IGrid?, Any?> = createGrid(id, player, stack, pos, slotId)
                ?: return
        NetworkHooks.openGui(player, GridContainerProvider(grid.left, grid.right), { buf ->
            buf.writeIdentifier(id)
            buf.writeBoolean(pos != null)
            if (pos != null) {
                buf.writeBlockPos(pos)
            }
            buf.writeBoolean(stack != null)
            if (stack != null) {
                buf.writeItemStack(stack)
            }
            buf.writeInt(slotId)
        })
    }

    @Nullable
    override fun createGrid(id: Identifier?, player: PlayerEntity?, @Nullable stack: ItemStack?, @Nullable pos: BlockPos?, slotId: Int): Pair<IGrid?, BlockEntity?>? {
        val factory = factories[id] ?: return null
        var grid: IGrid? = null
        val tile: BlockEntity? = factory.getRelevantTile(player!!.world, pos)
        when (factory.type) {
            GridFactoryType.STACK -> grid = factory.createFromStack(player, stack, slotId)
            GridFactoryType.BLOCK -> grid = factory.createFromBlock(player, pos)
        }
        return if (grid == null) {
            null
        } else Pair.of<IGrid?, BlockEntity?>(grid, tile)
    }
}
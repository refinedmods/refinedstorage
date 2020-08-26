package com.refinedmods.refinedstorage.tile.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.Text
import java.util.*

interface ICraftingMonitor {
    val title: Text
    fun onCancelled(player: ServerPlayerEntity?, @Nullable id: UUID?)
    val redstoneModeParameter: TileDataParameter<Int?, *>?
    val tasks: Collection<ICraftingTask?>?

    @get:Nullable
    val craftingManager: ICraftingManager?
    val isActiveOnClient: Boolean
    fun onClosed(player: PlayerEntity?)
    val tabSelected: Optional<UUID?>
    val tabPage: Int
    fun onTabSelectionChanged(taskId: Optional<UUID?>)
    fun onTabPageChanged(page: Int)
    val slotId: Int

    companion object {
        const val TABS_PER_PAGE = 7
    }
}
package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory
import com.refinedmods.refinedstorage.item.WirelessGridItem
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.function.Supplier

class PortableGridBlockItem(val type: Type) : EnergyBlockItem(
        if (type == Type.CREATIVE) BlockRegistryGenerated.CREATIVE_PORTABLE_GRID else BlockRegistryGenerated.PORTABLE_GRID,
        Settings().group(RS.MAIN_GROUP).maxCount(1),
        type == Type.CREATIVE,
        Supplier { RS.SERVER_CONFIG.portableGrid.getCapacity() }
) {
    enum class Type {
        NORMAL, CREATIVE
    }

    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        val stack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient) {
            instance().getGridManager()!!.openGrid(PortableGridGridFactory.ID, player as ServerPlayerEntity, stack, player.inventory.currentItem)
        }
        return ActionResult.resultSuccess(stack)
    }

    override fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        tooltip.add(TranslationTextComponent("block.refinedstorage.portable_grid.tooltip").setStyle(Styles.GRAY))
    }

    fun onItemUse(context: ItemUseContext): ActionResult {
        return if (!context.getPlayer().isCrouching()) {
            ActionResult.FAIL
        } else super.onItemUse(context)
    }

    fun getEntityLifespan(stack: ItemStack?, world: World?): Int {
        return Int.MAX_VALUE
    }

    fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        if (oldStack.item === newStack.item) {
            if (WirelessGridItem.Companion.getSortingDirection(oldStack) == WirelessGridItem.Companion.getSortingDirection(newStack) && WirelessGridItem.Companion.getSortingType(oldStack) == WirelessGridItem.Companion.getSortingType(newStack) && WirelessGridItem.Companion.getSearchBoxMode(oldStack) == WirelessGridItem.Companion.getSearchBoxMode(newStack) && WirelessGridItem.Companion.getTabSelected(oldStack) == WirelessGridItem.Companion.getTabSelected(newStack) && WirelessGridItem.Companion.getTabPage(oldStack) == WirelessGridItem.Companion.getTabPage(newStack) && WirelessGridItem.Companion.getSize(oldStack) == WirelessGridItem.Companion.getSize(newStack)) {
                return false
            }
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
    }

    init {
        this.setRegistryName(RS.ID, (if (type == Type.CREATIVE) "creative_" else "") + "portable_grid")
    }
}
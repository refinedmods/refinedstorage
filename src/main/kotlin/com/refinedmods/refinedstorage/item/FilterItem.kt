package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.container.FilterContainer
import com.refinedmods.refinedstorage.inventory.fluid.FilterFluidInventory
import com.refinedmods.refinedstorage.inventory.item.FilterItemsItemHandler
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidInstance

class FilterItem : Item(Properties().group(RS.MAIN_GROUP).maxStackSize(1)) {
    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        val stack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient) {
            if (player.isCrouching()) {
                return ActionResult(ActionResult.SUCCESS, ItemStack(RSItems.FILTER))
            }
            player.openContainer(object : INamedContainerProvider() {
                val displayName: Text
                    get() = TranslationTextComponent("gui.refinedstorage.filter")

                @Nullable
                fun createMenu(windowId: Int, inventory: PlayerInventory, player: PlayerEntity?): Container {
                    return FilterContainer(player!!, inventory.getCurrentItem(), windowId)
                }
            })
            return ActionResult(ActionResult.SUCCESS, stack)
        }
        return ActionResult(ActionResult.PASS, stack)
    }

    fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        tooltip.add(TranslationTextComponent("sidebutton.refinedstorage.mode." + if (getMode(stack) == IFilter.MODE_WHITELIST) "whitelist" else "blacklist").setStyle(Styles.YELLOW))
        if (isModFilter(stack)) {
            tooltip.add(TranslationTextComponent("gui.refinedstorage.filter.mod_filter").setStyle(Styles.BLUE))
        }
        val items = FilterItemsItemHandler(stack)
        RenderUtils.addCombinedItemsToTooltip(tooltip, false, items.filteredItems)
        val fluids = FilterFluidInventory(stack)
        RenderUtils.addCombinedFluidsToTooltip(tooltip, false, fluids.getFilteredFluids())
    }

    fun shouldCauseReequipAnimation(oldStack: ItemStack?, newStack: ItemStack?, slotChanged: Boolean): Boolean {
        return false
    }

    companion object {
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_MOD_FILTER = "ModFilter"
        private const val NBT_NAME = "Name"
        private const val NBT_ICON = "Icon"
        private const val NBT_FLUID_ICON = "FluidIcon"
        private const val NBT_TYPE = "Type"
        const val NBT_FLUID_FILTERS = "FluidFilters"
        @kotlin.jvm.JvmStatic
        fun getCompare(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_COMPARE)) stack.tag!!.getInt(NBT_COMPARE) else IComparer.COMPARE_NBT
        }

        @kotlin.jvm.JvmStatic
        fun setCompare(stack: ItemStack, compare: Int) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_COMPARE, compare)
        }

        @kotlin.jvm.JvmStatic
        fun getMode(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_MODE)) stack.tag!!.getInt(NBT_MODE) else IFilter.MODE_WHITELIST
        }

        @kotlin.jvm.JvmStatic
        fun setMode(stack: ItemStack, mode: Int) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_MODE, mode)
        }

        @kotlin.jvm.JvmStatic
        fun isModFilter(stack: ItemStack): Boolean {
            return stack.hasTag() && stack.tag!!.contains(NBT_MOD_FILTER) && stack.tag!!.getBoolean(NBT_MOD_FILTER)
        }

        @kotlin.jvm.JvmStatic
        fun setModFilter(stack: ItemStack, modFilter: Boolean) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putBoolean(NBT_MOD_FILTER, modFilter)
        }

        @kotlin.jvm.JvmStatic
        fun getName(stack: ItemStack): String {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_NAME)) stack.tag!!.getString(NBT_NAME) else ""
        }

        @kotlin.jvm.JvmStatic
        fun setName(stack: ItemStack, name: String?) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putString(NBT_NAME, name)
        }

        @Nonnull
        fun getIcon(stack: ItemStack): ItemStack {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_ICON)) ItemStack.read(stack.tag!!.getCompound(NBT_ICON)) else ItemStack.EMPTY
        }

        fun setIcon(stack: ItemStack, icon: ItemStack) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.put(NBT_ICON, icon.serializeNBT())
        }

        fun setFluidIcon(stack: ItemStack, @Nullable icon: FluidInstance?) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            if (icon == null) {
                stack.tag!!.remove(NBT_FLUID_ICON)
            } else {
                stack.tag!!.put(NBT_FLUID_ICON, icon.writeToNBT(CompoundTag()))
            }
        }

        @Nonnull
        fun getFluidIcon(stack: ItemStack): FluidInstance {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_FLUID_ICON)) FluidInstance.loadFluidInstanceFromNBT(stack.tag!!.getCompound(NBT_FLUID_ICON)) else FluidInstance.EMPTY
        }

        @kotlin.jvm.JvmStatic
        fun getType(stack: ItemStack): Int {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_TYPE)) stack.tag!!.getInt(NBT_TYPE) else IType.ITEMS
        }

        @kotlin.jvm.JvmStatic
        fun setType(stack: ItemStack, type: Int) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_TYPE, type)
        }
    }

    init {
        this.setRegistryName(RS.ID, "filter")
    }
}
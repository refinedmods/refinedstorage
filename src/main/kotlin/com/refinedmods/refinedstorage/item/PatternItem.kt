package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider
import com.refinedmods.refinedstorage.apiimpl.autocrafting.AllowedTagList
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPatternFactory
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.render.tesr.PatternItemStackTileRenderer
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidInstance
import java.util.*
import java.util.stream.Collectors

class PatternItem : Item(Properties().group(RS.MAIN_GROUP).setISTER({ { PatternItemStackTileRenderer() } })), ICraftingPatternProvider {
    fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        if (!stack.hasTag()) {
            return
        }
        val pattern = fromCache(world, stack)
        if (pattern!!.isValid()) {
            if (ContainerScreen.hasShiftDown() || isProcessing(stack)) {
                tooltip.add(TranslationTextComponent("misc.refinedstorage.pattern.inputs").setStyle(Styles.YELLOW))
                RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getInputs().stream().map<Any> { i: error.NonExistentClass -> if (i.size() > 0) i.get(0) else ItemStack.EMPTY }.collect(Collectors.toList<Any>()))
                RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidInputs().stream().map<Any> { i: error.NonExistentClass -> if (i.size() > 0) i.get(0) else FluidInstance.EMPTY }.collect(Collectors.toList()))
                tooltip.add(TranslationTextComponent("misc.refinedstorage.pattern.outputs").setStyle(Styles.YELLOW))
            }
            RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getOutputs())
            RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidOutputs())
            if (pattern.allowedTagList != null) {
                for (i in pattern.allowedTagList.getAllowedItemTags().indices) {
                    val allowedTags: Set<Identifier> = pattern.allowedTagList.getAllowedItemTags()[i]
                    for (tag in allowedTags) {
                        tooltip.add(TranslationTextComponent(
                                "misc.refinedstorage.pattern.allowed_item_tag",
                                tag.toString(),
                                pattern.getInputs()[i].get(0).getDisplayName()
                        ).setStyle(Styles.AQUA))
                    }
                }
                for (i in pattern.allowedTagList.getAllowedFluidTags().indices) {
                    val allowedTags: Set<Identifier> = pattern.allowedTagList.getAllowedFluidTags()[i]
                    for (tag in allowedTags) {
                        tooltip.add(TranslationTextComponent(
                                "misc.refinedstorage.pattern.allowed_fluid_tag",
                                tag.toString(),
                                pattern.getFluidInputs()[i].get(0).getDisplayName()
                        ).setStyle(Styles.AQUA))
                    }
                }
            }
            if (isExact(stack)) {
                tooltip.add(TranslationTextComponent("misc.refinedstorage.pattern.exact").setStyle(Styles.BLUE))
            }
            if (isProcessing(stack)) {
                tooltip.add(TranslationTextComponent("misc.refinedstorage.processing").setStyle(Styles.BLUE))
            }
        } else {
            tooltip.add(TranslationTextComponent("misc.refinedstorage.pattern.invalid").setStyle(Styles.RED))
            tooltip.add(pattern.getErrorMessage().copyRaw().setStyle(Styles.GRAY))
        }
    }

    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        return if (!world.isClient && player.isCrouching()) {
            ActionResult(ActionResult.SUCCESS, ItemStack(RSItems.PATTERN, player.getHeldItem(hand).getCount()))
        } else ActionResult(ActionResult.PASS, player.getHeldItem(hand))
    }

    @Nonnull
    override fun create(world: World?, stack: ItemStack?, container: ICraftingPatternContainer?): ICraftingPattern? {
        return CraftingPatternFactory.INSTANCE.create(world!!, container!!, stack!!)
    }

    companion object {
        private val CACHE: MutableMap<ItemStack, CraftingPattern?> = HashMap()
        private const val NBT_VERSION = "Version"
        private const val NBT_INPUT_SLOT = "Input_%d"
        private const val NBT_OUTPUT_SLOT = "Output_%d"
        private const val NBT_FLUID_INPUT_SLOT = "FluidInput_%d"
        private const val NBT_FLUID_OUTPUT_SLOT = "FluidOutput_%d"
        private const val NBT_EXACT = "Exact"
        private const val NBT_PROCESSING = "Processing"
        private const val NBT_ALLOWED_TAGS = "AllowedTags"
        private const val VERSION = 1
        @kotlin.jvm.JvmStatic
        fun fromCache(world: World?, stack: ItemStack): CraftingPattern? {
            if (!CACHE.containsKey(stack)) {
                CACHE[stack] = CraftingPatternFactory.INSTANCE.create(world!!, null, stack)
            }
            return CACHE[stack]
        }

        fun setInputSlot(pattern: ItemStack, slot: Int, stack: ItemStack) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.put(String.format(NBT_INPUT_SLOT, slot), stack.serializeNBT())
        }

        @Nonnull
        fun getInputSlot(pattern: ItemStack, slot: Int): ItemStack {
            val id = String.format(NBT_INPUT_SLOT, slot)
            return if (!pattern.hasTag() || !pattern.tag!!.contains(id)) {
                ItemStack.EMPTY
            } else ItemStack.read(pattern.tag!!.getCompound(id))
        }

        fun setOutputSlot(pattern: ItemStack, slot: Int, stack: ItemStack) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.put(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT())
        }

        @Nonnull
        fun getOutputSlot(pattern: ItemStack, slot: Int): ItemStack {
            val id = String.format(NBT_OUTPUT_SLOT, slot)
            return if (!pattern.hasTag() || !pattern.tag!!.contains(id)) {
                ItemStack.EMPTY
            } else ItemStack.read(pattern.tag!!.getCompound(id))
        }

        fun setFluidInputSlot(pattern: ItemStack, slot: Int, stack: FluidInstance) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.put(String.format(NBT_FLUID_INPUT_SLOT, slot), stack.writeToNBT(CompoundTag()))
        }

        fun getFluidInputSlot(pattern: ItemStack, slot: Int): FluidInstance {
            val id = String.format(NBT_FLUID_INPUT_SLOT, slot)
            return if (!pattern.hasTag() || !pattern.tag!!.contains(id)) {
                FluidInstance.EMPTY
            } else FluidInstance.loadFluidInstanceFromNBT(pattern.tag!!.getCompound(id))
        }

        fun setFluidOutputSlot(pattern: ItemStack, slot: Int, stack: FluidInstance) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.put(String.format(NBT_FLUID_OUTPUT_SLOT, slot), stack.writeToNBT(CompoundTag()))
        }

        fun getFluidOutputSlot(pattern: ItemStack, slot: Int): FluidInstance {
            val id = String.format(NBT_FLUID_OUTPUT_SLOT, slot)
            return if (!pattern.hasTag() || !pattern.tag!!.contains(id)) {
                FluidInstance.EMPTY
            } else FluidInstance.loadFluidInstanceFromNBT(pattern.tag!!.getCompound(id))
        }

        fun isProcessing(pattern: ItemStack): Boolean {
            return pattern.hasTag() && pattern.tag!!.contains(NBT_PROCESSING) && pattern.tag!!.getBoolean(NBT_PROCESSING)
        }

        fun setProcessing(pattern: ItemStack, processing: Boolean) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.putBoolean(NBT_PROCESSING, processing)
        }

        fun isExact(pattern: ItemStack): Boolean {
            return if (!pattern.hasTag() || !pattern.tag!!.contains(NBT_EXACT)) {
                false
            } else pattern.tag!!.getBoolean(NBT_EXACT)
        }

        fun setExact(pattern: ItemStack, exact: Boolean) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.putBoolean(NBT_EXACT, exact)
        }

        fun setToCurrentVersion(pattern: ItemStack) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.putInt(NBT_VERSION, VERSION)
        }

        fun setAllowedTags(pattern: ItemStack, allowedTagList: AllowedTagList) {
            if (!pattern.hasTag()) {
                pattern.tag = CompoundTag()
            }
            pattern.tag!!.put(NBT_ALLOWED_TAGS, allowedTagList.writeToNbt())
        }

        @Nullable
        fun getAllowedTags(pattern: ItemStack): AllowedTagList? {
            if (!pattern.hasTag() || !pattern.tag!!.contains(NBT_ALLOWED_TAGS)) {
                return null
            }
            val allowedTagList = AllowedTagList(null)
            allowedTagList.readFromNbt(pattern.tag!!.getCompound(NBT_ALLOWED_TAGS))
            return allowedTagList
        }
    }

    init {
        this.setRegistryName(RS.ID, "pattern")
    }
}
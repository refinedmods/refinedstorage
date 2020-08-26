package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

class NetworkCardItem : Item(Properties().group(RS.MAIN_GROUP).maxStackSize(1)) {
    fun onItemUse(ctx: ItemUseContext): ActionResult {
        val block: Block = ctx.getWorld().getBlockState(ctx.getPos()).getBlock()
        if (block === RSBlocks.NETWORK_RECEIVER) {
            val tag = CompoundTag()
            tag.putInt(NBT_RECEIVER_X, ctx.getPos().getX())
            tag.putInt(NBT_RECEIVER_Y, ctx.getPos().getY())
            tag.putInt(NBT_RECEIVER_Z, ctx.getPos().getZ())
            tag.putString(NBT_DIMENSION, ctx.getWorld().func_234923_W_().func_240901_a_().toString())
            ctx.getPlayer().getHeldItem(ctx.getHand()).setTag(tag)
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        val pos = getReceiver(stack)
        val type: RegistryKey<World>? = getDimension(stack)
        if (pos != null && type != null) {
            tooltip.add(TranslationTextComponent(
                    "misc.refinedstorage.network_card.tooltip",
                    pos.x,
                    pos.y,
                    pos.z,
                    type.func_240901_a_().toString()
            ).setStyle(Styles.GRAY))
        }
    }

    companion object {
        private const val NBT_RECEIVER_X = "ReceiverX"
        private const val NBT_RECEIVER_Y = "ReceiverY"
        private const val NBT_RECEIVER_Z = "ReceiverZ"
        private const val NBT_DIMENSION = "Dimension"
        @Nullable
        fun getReceiver(stack: ItemStack): BlockPos? {
            return if (stack.hasTag() &&
                    stack.tag!!.contains(NBT_RECEIVER_X) &&
                    stack.tag!!.contains(NBT_RECEIVER_Y) &&
                    stack.tag!!.contains(NBT_RECEIVER_Z)) {
                BlockPos(
                        stack.tag!!.getInt(NBT_RECEIVER_X),
                        stack.tag!!.getInt(NBT_RECEIVER_Y),
                        stack.tag!!.getInt(NBT_RECEIVER_Z)
                )
            } else null
        }

        @Nullable
        fun getDimension(stack: ItemStack): RegistryKey<World>? {
            if (stack.hasTag() && stack.tag!!.contains(NBT_DIMENSION)) {
                val name: Identifier = Identifier.tryCreate(stack.tag!!.getString(NBT_DIMENSION))
                        ?: return null
                return RegistryKey.func_240903_a_(Registry.WORLD_KEY, name)
            }
            return null
        }
    }

    init {
        this.setRegistryName(RS.ID, "network_card")
    }
}
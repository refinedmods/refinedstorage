package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.block.NetworkReceiverBlock;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class NetworkCardItem extends Item {
    private static final String NBT_RECEIVER_X = "ReceiverX";
    private static final String NBT_RECEIVER_Y = "ReceiverY";
    private static final String NBT_RECEIVER_Z = "ReceiverZ";
    private static final String NBT_DIMENSION = "Dimension";

    public NetworkCardItem() {
        super(new Item.Properties().tab(RS.CREATIVE_TAB).stacksTo(1));
    }

    @Nullable
    public static BlockPos getReceiver(ItemStack stack) {
        if (stack.hasTag() &&
            stack.getTag().contains(NBT_RECEIVER_X) &&
            stack.getTag().contains(NBT_RECEIVER_Y) &&
            stack.getTag().contains(NBT_RECEIVER_Z)) {
            return new BlockPos(
                stack.getTag().getInt(NBT_RECEIVER_X),
                stack.getTag().getInt(NBT_RECEIVER_Y),
                stack.getTag().getInt(NBT_RECEIVER_Z)
            );
        }

        return null;
    }

    @Nullable
    public static ResourceKey<Level> getDimension(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryParse(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }

            return ResourceKey.create(Registry.DIMENSION_REGISTRY, name);
        }

        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Block block = ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock();

        if (block instanceof NetworkReceiverBlock) {
            CompoundTag tag = new CompoundTag();

            tag.putInt(NBT_RECEIVER_X, ctx.getClickedPos().getX());
            tag.putInt(NBT_RECEIVER_Y, ctx.getClickedPos().getY());
            tag.putInt(NBT_RECEIVER_Z, ctx.getClickedPos().getZ());
            tag.putString(NBT_DIMENSION, ctx.getLevel().dimension().location().toString());

            ctx.getPlayer().getItemInHand(ctx.getHand()).setTag(tag);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        BlockPos pos = getReceiver(stack);
        ResourceKey<Level> type = getDimension(stack);

        if (pos != null && type != null) {
            tooltip.add(new TranslatableComponent(
                "misc.refinedstorage.network_card.tooltip",
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                type.location().toString()
            ).setStyle(Styles.GRAY));
        }
    }
}

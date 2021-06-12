package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.block.NetworkReceiverBlock;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class NetworkCardItem extends Item {
    private static final String NBT_RECEIVER_X = "ReceiverX";
    private static final String NBT_RECEIVER_Y = "ReceiverY";
    private static final String NBT_RECEIVER_Z = "ReceiverZ";
    private static final String NBT_DIMENSION = "Dimension";

    public NetworkCardItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        Block block = ctx.getWorld().getBlockState(ctx.getPos()).getBlock();

        if (block instanceof NetworkReceiverBlock) {
            CompoundNBT tag = new CompoundNBT();

            tag.putInt(NBT_RECEIVER_X, ctx.getPos().getX());
            tag.putInt(NBT_RECEIVER_Y, ctx.getPos().getY());
            tag.putInt(NBT_RECEIVER_Z, ctx.getPos().getZ());
            tag.putString(NBT_DIMENSION, ctx.getWorld().getDimensionKey().getLocation().toString());

            ctx.getPlayer().getHeldItem(ctx.getHand()).setTag(tag);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        BlockPos pos = getReceiver(stack);
        RegistryKey<World> type = getDimension(stack);

        if (pos != null && type != null) {
            tooltip.add(new TranslationTextComponent(
                "misc.refinedstorage.network_card.tooltip",
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                type.getLocation().toString()
            ).setStyle(Styles.GRAY));
        }
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
    public static RegistryKey<World> getDimension(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryCreate(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }

            return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, name);
        }

        return null;
    }
}

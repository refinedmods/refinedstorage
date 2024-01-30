package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.block.CableBlock;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CoverItem extends Item {
    private static final String NBT_ITEM = "Item";

    public CoverItem() {
        super(new Item.Properties());
    }

    public static void setItem(ItemStack cover, ItemStack item) {
        if (!cover.hasTag()) {
            cover.setTag(new CompoundTag());
        }
        ItemStack result = item.copy();
        result.setCount(1);
        cover.getTag().put(NBT_ITEM, result.save(new CompoundTag()));
    }

    @Nonnull
    public static ItemStack getItem(ItemStack cover) {
        if (!cover.hasTag() || !cover.getTag().contains(NBT_ITEM)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(cover.getTag().getCompound(NBT_ITEM));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ItemStack item = getItem(stack);

        if (!item.isEmpty()) {
            tooltip.add(item.getItem().getName(item).copy().withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Level level = context.getLevel();

        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        BlockEntity blockEntity = level.getBlockEntity(pos);

        // Support placing on the bottom side without too much hassle.
        if (!canPlaceOn(level, pos, facing)) {
            pos = pos.above();

            facing = Direction.DOWN;

            blockEntity = level.getBlockEntity(pos);
        }

        if (canPlaceOn(level, pos, facing)) {
            if (level.isClientSide) {
                level.getModelDataManager().requestRefresh(blockEntity);
                return InteractionResult.SUCCESS;
            }

            INetworkNode node = ((NetworkNodeBlockEntity<?>) blockEntity).getNode();

            if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, context.getPlayer())) {
                LevelUtils.sendNoPermissionMessage(context.getPlayer());

                return InteractionResult.FAIL;
            }

            if (((ICoverable) node).getCoverManager().setCover(facing, createCover(getItem(stack)))) {
                context.getPlayer().getItemInHand(context.getHand()).shrink(1);

                LevelUtils.updateBlock(level, pos);
                API.instance().getNetworkNodeManager((ServerLevel) level).markForSaving();
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    private boolean canPlaceOn(Level level, BlockPos pos, Direction facing) {
        return level.getBlockEntity(pos) instanceof NetworkNodeBlockEntity
            && ((NetworkNodeBlockEntity<?>) level.getBlockEntity(pos)).getNode() instanceof ICoverable
            && !CableBlock.hasVisualConnectionOnSide(level.getBlockState(pos), facing);
    }

    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.NORMAL);
    }
}

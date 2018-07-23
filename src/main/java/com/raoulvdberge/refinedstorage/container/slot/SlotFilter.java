package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotFilter extends SlotItemHandler {
    public static final int FILTER_ALLOW_SIZE = 1;
    public static final int FILTER_ALLOW_BLOCKS = 2;

    private int flags = 0;

    public SlotFilter(IItemHandler handler, int id, int x, int y, int flags) {
        super(handler, id, x, y);

        this.flags = flags;
    }

    public SlotFilter(IItemHandler handler, int id, int x, int y) {
        this(handler, id, x, y, 0);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        if (super.isItemValid(stack)) {
            if (isBlockAllowed()) {
                return stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemBlockSpecial || stack.getItem() instanceof IPlantable || stack.getItem() instanceof ItemSkull;
            }

            return true;
        }

        return false;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && !isSizeAllowed()) {
            stack.setCount(1);
        }

        super.putStack(stack);
    }

    public boolean isSizeAllowed() {
        return (flags & FILTER_ALLOW_SIZE) == FILTER_ALLOW_SIZE;
    }

    public boolean isBlockAllowed() {
        return (flags & FILTER_ALLOW_BLOCKS) == FILTER_ALLOW_BLOCKS;
    }

    public int getModifiedAmount(int dragType) {
        int amount = getStack().getCount();

        if (dragType == 0) {
            amount = Math.max(1, amount - 1);
        } else if (dragType == 1) {
            amount = Math.min(getStack().getMaxStackSize(), amount + 1);
        }

        return amount;
    }

    public int getInitialAmount(ItemStack stack) {
        return stack.getCount();
    }

    @Nullable
    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos, @Nullable ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            if (item instanceof ItemBlockSpecial) {
                return ((ItemBlockSpecial) item).getBlock().getDefaultState();
            } else if (item instanceof ItemBlock) {
                return (((ItemBlock) item).getBlock()).getDefaultState();
            } else if (item instanceof IPlantable) {
                return ((IPlantable) item).getPlant(world, pos);
            } else if (item instanceof ItemSkull) {
                return Blocks.SKULL.getDefaultState();
            }
        }

        return null;
    }
}

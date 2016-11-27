package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

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
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        if (super.isItemValid(stack)) {
            if (allowsBlocks()) {
                return stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemBlockSpecial || stack.getItem() instanceof IPlantable || stack.getItem() instanceof ItemSkull;
            }

            return true;
        }

        return false;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && !allowsSize()) {
            stack.setCount(1);
        }

        super.putStack(stack);
    }

    public boolean allowsSize() {
        return (flags & FILTER_ALLOW_SIZE) == FILTER_ALLOW_SIZE;
    }

    public boolean allowsBlocks() {
        return (flags & FILTER_ALLOW_BLOCKS) == FILTER_ALLOW_BLOCKS;
    }

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos, ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            if (item instanceof ItemBlockSpecial) {
                try {
                    Field f = ((ItemBlockSpecial) item).getClass().getDeclaredField("block");
                    f.setAccessible(true);
                    return ((Block) f.get(item)).getDefaultState();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    // NO OP
                }
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

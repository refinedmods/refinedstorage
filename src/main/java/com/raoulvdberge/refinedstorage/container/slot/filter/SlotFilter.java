package com.raoulvdberge.refinedstorage.container.slot.filter;

import com.raoulvdberge.refinedstorage.container.slot.SlotBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotFilter extends SlotBase {
    public static final int FILTER_ALLOW_SIZE = 1;
    public static final int FILTER_ALLOW_BLOCKS = 2;

    private int flags = 0;

    public SlotFilter(IItemHandler handler, int inventoryIndex, int x, int y, int flags) {
        super(handler, inventoryIndex, x, y);

        this.flags = flags;
    }

    public SlotFilter(IItemHandler handler, int inventoryIndex, int x, int y) {
        this(handler, inventoryIndex, x, y, 0);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        if (super.isItemValid(stack)) {
            if (isBlockAllowed()) {
                // TODO!
                return stack.getItem() instanceof BlockItem || /*stack.getItem() instanceof ItemBlockSpecial ||*/ stack.getItem() instanceof IPlantable || stack.getItem() instanceof SkullItem;
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

    @Nullable
    public static BlockState getBlockState(World world, BlockPos pos, @Nullable ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            // TODO if (item instanceof ItemBlockSpecial) {
            //    return ((ItemBlockSpecial) item).getBlock().getDefaultState();
            /*} else*/
            /*if (item instanceof SkullItem) {
                return Blocks.SKELETON_SKULL.getDefaultState();
            } else */
            if (item instanceof BlockItem) {
                return (((BlockItem) item).getBlock()).getDefaultState();
            } else if (item instanceof IPlantable) {
                return ((IPlantable) item).getPlant(world, pos);
            }
        }

        return null;
    }
}

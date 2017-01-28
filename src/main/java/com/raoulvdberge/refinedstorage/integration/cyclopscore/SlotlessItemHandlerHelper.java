package com.raoulvdberge.refinedstorage.integration.cyclopscore;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;

import javax.annotation.Nonnull;

public class SlotlessItemHandlerHelper {
    public static boolean isSlotless(TileEntity entity, EnumFacing facing) {
        return entity != null && entity.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ISlotlessItemHandler getSlotlessHandler(TileEntity tile, EnumFacing facing) {
        return tile.getCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ItemStack insertItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, boolean simulate) {
        return insertItem(getSlotlessHandler(tile, facing), stack, stack.getCount(), simulate);
    }

    public static ItemStack insertItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, int size, boolean simulate) {
        return insertItem(getSlotlessHandler(tile, facing), stack, size, simulate);
    }

    public static ItemStack insertItem(ISlotlessItemHandler handler, @Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack remainder = handler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        int remainderCount = -1;

        while (!remainder.isEmpty() && remainder.getCount() != remainderCount) {
            remainderCount = remainder.getCount();
            remainder = handler.insertItem(remainder.copy(), simulate);
        }

        return remainder;
    }

    public static ItemStack extractItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        return extractItem(getSlotlessHandler(tile, facing), stack, size, flags, simulate);
    }

    public static ItemStack extractItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, int size, boolean simulate) {
        return extractItem(getSlotlessHandler(tile, facing), stack, size, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler handler, @Nonnull ItemStack stack, int size, boolean simulate) {
        return extractItem(handler, stack, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler handler, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        ItemStack extracted = handler.extractItem(ItemHandlerHelper.copyStackWithSize(stack, size), CyclopsComparer.comparerFlagsToItemMatch(flags), simulate);

        while (!extracted.isEmpty() && extracted.getCount() < size) {
            ItemStack extraExtract = handler.extractItem(ItemHandlerHelper.copyStackWithSize(extracted, size - extracted.getCount()), CyclopsComparer.comparerFlagsToItemMatch(flags), simulate);

            if (!extraExtract.isEmpty()) {
                extracted.grow(extraExtract.getCount());
            } else {
                // Nothing more to extract
                break;
            }
        }

        return extracted;
    }

    public static ItemStack extractItem(TileEntity tile, EnumFacing facing, int size, boolean simulate) {
        return extractItem(getSlotlessHandler(tile, facing), size, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler handler, int size, boolean simulate) {
        return handler.extractItem(size, simulate);
    }
}

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
        return entity.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ISlotlessItemHandler getSlotlessHandler(TileEntity entity, EnumFacing facing) {
        return entity.getCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ItemStack insertItem(TileEntity entity, EnumFacing facing, @Nonnull ItemStack stack, boolean simulate) {
        return insertItem(getSlotlessHandler(entity, facing), stack, stack.stackSize, simulate);
    }

    public static ItemStack insertItem(TileEntity entity, EnumFacing facing, @Nonnull ItemStack stack, int size, boolean simulate) {
        return insertItem(getSlotlessHandler(entity, facing), stack, size, simulate);
    }

    public static ItemStack insertItem(ISlotlessItemHandler slotlessItemHandler, @Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack remainder = slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        int remainderCount = -1;
        while (remainder != null && remainder.stackSize != remainderCount)
        {
            remainderCount = remainder.stackSize;
            remainder = slotlessItemHandler.insertItem(remainder.copy(), simulate);
        }
        return remainder;
    }

    public static ItemStack extractItem(TileEntity entity, EnumFacing facing, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        return extractItem(getSlotlessHandler(entity, facing), stack, size, flags, simulate);
    }

    public static ItemStack extractItem(TileEntity entity, EnumFacing facing, @Nonnull ItemStack stack, int size, boolean simulate) {
        return extractItem(getSlotlessHandler(entity, facing), stack, size, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler slotlessItemHandler, @Nonnull ItemStack stack, int size, boolean simulate) {
        return extractItem(slotlessItemHandler, stack, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler slotlessItemHandler, @Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        ItemStack extracted = slotlessItemHandler.extractItem(ItemHandlerHelper.copyStackWithSize(stack, size), CyclopsComparer.comparerFlagsToItemMatch(flags), simulate);
        while (extracted.stackSize < size) {
            ItemStack extraExtract = slotlessItemHandler.extractItem(ItemHandlerHelper.copyStackWithSize(extracted, size - extracted.stackSize), CyclopsComparer.comparerFlagsToItemMatch(flags), simulate);
            if (extraExtract != null) {
                extracted.stackSize += extraExtract.stackSize;
            } else {
                // Nothing more to extract
                break;
            }
        }
        return extracted;
    }

    public static ItemStack extractItem(TileEntity entity, EnumFacing facing, int size, boolean simulate) {
        return extractItem(getSlotlessHandler(entity, facing), size, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler slotlessItemHandler, int size, boolean simulate) {
        return slotlessItemHandler.extractItem(size, simulate);
    }
}

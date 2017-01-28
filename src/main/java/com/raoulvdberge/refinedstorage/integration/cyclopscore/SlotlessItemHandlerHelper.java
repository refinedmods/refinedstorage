package com.raoulvdberge.refinedstorage.integration.cyclopscore;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.util.Comparer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class SlotlessItemHandlerHelper {
    public static boolean isSlotless(TileEntity entity, EnumFacing facing) {
        return entity != null && entity.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ISlotlessItemHandler getSlotlessHandler(TileEntity tile, EnumFacing facing) {
        return tile.getCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }

    public static ItemStack insertItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, boolean simulate) {
        return insertItem(getSlotlessHandler(tile, facing), stack, stack.stackSize, simulate);
    }

    public static ItemStack insertItem(TileEntity tile, EnumFacing facing, @Nonnull ItemStack stack, int size, boolean simulate) {
        return insertItem(getSlotlessHandler(tile, facing), stack, size, simulate);
    }

    public static ItemStack insertItem(ISlotlessItemHandler handler, @Nonnull ItemStack stack, int size, boolean simulate) {
        ItemStack remainder = handler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        int remainderCount = -1;

        while (remainder != null && remainder.stackSize != remainderCount) {
            remainderCount = remainder.stackSize;

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
        int compare = CyclopsComparer.comparerFlagsToItemMatch(flags, stack.getMetadata() == OreDictionary.WILDCARD_VALUE);
        stack = ItemHandlerHelper.copyStackWithSize(stack, size);
        if ((flags & IComparer.COMPARE_STRIP_NBT) == IComparer.COMPARE_STRIP_NBT) {
            stack = Comparer.stripTags(stack);
        }
        ItemStack extracted = handler.extractItem(stack, compare, simulate);

        if (extracted == null && (flags & IComparer.COMPARE_OREDICT) == IComparer.COMPARE_OREDICT) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                Iterator<ItemStack> itr = OreDictionary.getOres(OreDictionary.getOreName(id)).iterator();
                while (itr.hasNext() && extracted == null) {
                    ItemStack oreStack = itr.next();
                    int oreCompare = ItemMatch.NBT | (oreStack.getMetadata() == OreDictionary.WILDCARD_VALUE ? 0 : ItemMatch.DAMAGE);
                    extracted = handler.extractItem(oreStack, oreCompare, simulate);
                }
                if (extracted != null) {
                    // We found one. Woooo!
                    break;
                }
            }
        }

        if (extracted != null) {
            while (extracted.stackSize < size) {
                ItemStack extraExtract = handler.extractItem(ItemHandlerHelper.copyStackWithSize(extracted, size - extracted.stackSize), compare, simulate);

                if (extraExtract != null) {
                    extracted.stackSize += extraExtract.stackSize;
                } else {
                    // Nothing more to extract
                    break;
                }
            }
        }

        return extracted;
    }

    public static ItemStack extractItem(TileEntity tile, EnumFacing facing, int size, boolean simulate) {
        return extractItem(getSlotlessHandler(tile, facing), size, simulate);
    }

    public static ItemStack extractItem(ISlotlessItemHandler slotlessItemHandler, int size, boolean simulate) {
        return slotlessItemHandler.extractItem(size, simulate);
    }
}

package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class ImportingBehaviorItemHandler implements IImportingBehavior {
    public static final IImportingBehavior INSTANCE = new ImportingBehaviorItemHandler();

    @Override
    public int doImport(TileEntity entity, EnumFacing facing, int currentSlot, IItemHandler itemFilters, int mode, int compare, int ticks, ItemHandlerUpgrade upgrades, INetworkMaster network) {
        IItemHandler handler = RSUtils.getItemHandler(entity, facing);

        if (entity instanceof TileDiskDrive || handler == null) {
            return currentSlot;
        }

        if (currentSlot >= handler.getSlots()) {
            currentSlot = 0;
        }

        if (handler.getSlots() > 0) {
            ItemStack stack = handler.getStackInSlot(currentSlot);

            if (stack.isEmpty() || !IFilterable.canTake(itemFilters, mode, compare, stack)) {
                currentSlot++;
            } else if (ticks % upgrades.getSpeed() == 0) {
                ItemStack result = handler.extractItem(currentSlot, upgrades.getItemInteractCount(), true);

                if (!result.isEmpty() && network.insertItem(result, result.getCount(), true) == null) {
                    network.insertItemTracked(result, result.getCount());

                    handler.extractItem(currentSlot, upgrades.getItemInteractCount(), false);
                } else {
                    currentSlot++;
                }
            }
        }

        return currentSlot;
    }
}

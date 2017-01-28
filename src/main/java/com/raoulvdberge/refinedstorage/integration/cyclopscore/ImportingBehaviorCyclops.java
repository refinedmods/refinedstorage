package com.raoulvdberge.refinedstorage.integration.cyclopscore;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.inventory.IImportingBehavior;
import com.raoulvdberge.refinedstorage.inventory.ImportingBehaviorItemHandler;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public class ImportingBehaviorCyclops implements IImportingBehavior {

    public static final IImportingBehavior INSTANCE = new ImportingBehaviorCyclops();

    @Override
    public int doImport(TileEntity entity, EnumFacing facing, int currentSlot, IItemHandler itemFilters, int mode, int compare, int ticks, ItemHandlerUpgrade upgrades, INetworkMaster network) {
        if (IFilterable.isEmpty(itemFilters)) {
            if (ticks % upgrades.getSpeed() == 0) {
                ItemStack result = SlotlessItemHandlerHelper.extractItem(entity, facing, upgrades.getInteractStackSize(), true);
                if (result != null && network.insertItem(result, result.stackSize, true) == null) {
                    network.insertItem(result, result.stackSize, false);
                    SlotlessItemHandlerHelper.extractItem(entity, facing, result.copy(), upgrades.getInteractStackSize(), false);
                }
            }
        } else {
            return ImportingBehaviorItemHandler.INSTANCE.doImport(entity, facing, currentSlot, itemFilters, mode, compare, ticks, upgrades, network);
        }
        return 0;
    }
}

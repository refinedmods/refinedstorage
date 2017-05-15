package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public interface IImportingBehavior {
    int doImport(TileEntity entity, EnumFacing facing, int currentSlot, IItemHandler itemFilters, int mode, int compare, int ticks, ItemHandlerUpgrade upgrades, INetwork network);
}

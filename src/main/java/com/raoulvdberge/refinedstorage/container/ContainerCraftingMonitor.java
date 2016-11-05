package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCraftingMonitor extends ContainerBase {
    private ICraftingMonitor craftingMonitor;

    public ContainerCraftingMonitor(ICraftingMonitor craftingMonitor, EntityPlayer player) {
        super(craftingMonitor instanceof TileCraftingMonitor ? (TileCraftingMonitor) craftingMonitor : null, player);

        this.craftingMonitor = craftingMonitor;

        addPlayerInventory(8, 148);
    }

    public ICraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }
}

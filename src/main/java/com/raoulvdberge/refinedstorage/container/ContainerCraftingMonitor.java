package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCraftingMonitor extends ContainerBase {
    private ICraftingMonitor craftingMonitor;

    public ContainerCraftingMonitor(ICraftingMonitor craftingMonitor, EntityPlayer player) {
        super(null, player);

        this.craftingMonitor = craftingMonitor;

        addPlayerInventory(8, 148);
    }

    public ICraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }
}

package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCraftingMonitor extends ContainerBase {
    public ContainerCraftingMonitor(TileCraftingMonitor craftingMonitor, EntityPlayer player) {
        super(craftingMonitor, player);

        addPlayerInventory(8, 148);
    }
}

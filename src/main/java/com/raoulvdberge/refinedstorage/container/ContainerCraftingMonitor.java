package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCraftingMonitor extends ContainerBase {
    private ICraftingMonitor craftingMonitor;

    public ContainerCraftingMonitor(ICraftingMonitor craftingMonitor, EntityPlayer player) {
        super(craftingMonitor instanceof TileCraftingMonitor ? (TileCraftingMonitor) craftingMonitor : null, player);

        this.craftingMonitor = craftingMonitor;

        addPlayerInventory(8, 148);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(craftingMonitor.getFilter(), i, 187, 6 + (18 * i)));
        }
    }

    public ICraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }
}

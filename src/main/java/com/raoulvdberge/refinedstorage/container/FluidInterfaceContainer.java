package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class FluidInterfaceContainer extends BaseContainer {
    public FluidInterfaceContainer(TileFluidInterface fluidInterface, PlayerEntity player, int windowId) {
        super(RSContainers.FLUID_INTERFACE, fluidInterface, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(fluidInterface.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlot(new SlotItemHandler(fluidInterface.getNode().getIn(), 0, 44, 32));
        addSlot(new FluidFilterSlot(fluidInterface.getNode().getOut(), 0, 116, 32, FilterSlot.FILTER_ALLOW_SIZE));

        addPlayerInventory(8, 122);

        transferManager.addBiTransfer(player.inventory, fluidInterface.getNode().getIn());
        transferManager.addFluidFilterTransfer(player.inventory, fluidInterface.getNode().getOut());
    }
}

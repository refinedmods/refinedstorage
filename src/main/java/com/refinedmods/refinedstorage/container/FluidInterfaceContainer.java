package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class FluidInterfaceContainer extends BaseContainer {
    public FluidInterfaceContainer(FluidInterfaceTile fluidInterface, Player player, int windowId) {
        super(RSContainers.FLUID_INTERFACE, fluidInterface, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(fluidInterface.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlot(new SlotItemHandler(fluidInterface.getNode().getIn(), 0, 44, 32));
        addSlot(new FluidFilterSlot(fluidInterface.getNode().getOut(), 0, 116, 32, FilterSlot.FILTER_ALLOW_SIZE));

        addPlayerInventory(8, 122);

        transferManager.addBiTransfer(player.getInventory(), fluidInterface.getNode().getIn());
        transferManager.addFluidFilterTransfer(player.getInventory(), fluidInterface.getNode().getOut());
    }
}

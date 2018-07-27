package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFluidInterface extends ContainerBase {
    public ContainerFluidInterface(TileFluidInterface fluidInterface, EntityPlayer player) {
        super(fluidInterface, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(fluidInterface.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotItemHandler(fluidInterface.getNode().getIn(), 0, 44, 32));
        addSlotToContainer(new SlotFilterFluid(fluidInterface.getNode().getOut(), 0, 116, 32, SlotFilter.FILTER_ALLOW_SIZE));

        addPlayerInventory(8, 122);

        transferManager.addBiTransfer(player.inventory, fluidInterface.getNode().getIn());
        transferManager.addFluidFilterTransfer(player.inventory, fluidInterface.getNode().getOut());
    }
}

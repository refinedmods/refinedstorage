package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDetector;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDetector extends ContainerBase {
    private NetworkNodeDetector detector;

    public ContainerDetector(TileDetector detector, EntityPlayer player) {
        super(detector, player);

        this.detector = detector.getNode();

        addSlotToContainer(new SlotFilter(detector.getNode().getItemFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.ITEMS));
        addSlotToContainer(new SlotFilterFluid(detector.getNode().getFluidFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        ItemStack stack = slot.getStack();

        if (slot.getHasStack() && index > 0) {
            if (detector.getType() == IType.ITEMS) {
                return transferToFilters(stack, 0, 1);
            } else {
                return transferToFluidFilters(stack);
            }
        }

        return ItemStack.EMPTY;
    }
}

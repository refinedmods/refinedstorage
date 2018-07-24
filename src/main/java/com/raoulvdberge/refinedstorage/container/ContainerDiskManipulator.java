package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator.NetworkNodeDiskManipulator;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDiskManipulator extends ContainerBase {
    private NetworkNodeDiskManipulator diskManipulator;

    public ContainerDiskManipulator(TileDiskManipulator diskManipulator, EntityPlayer player) {
        super(diskManipulator, player);

        this.diskManipulator = diskManipulator.getNode();

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(diskManipulator.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotItemHandler(diskManipulator.getNode().getInputDisks(), i, 44, 57 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotItemHandler(diskManipulator.getNode().getOutputDisks(), i, 116, 57 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(diskManipulator.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskManipulator.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(diskManipulator.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskManipulator.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 129);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4 + 3 + 3) {
                if (!mergeItemStack(stack, 4 + 3 + 3 + 9 + 9, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4 + 3, false)) {
                if (diskManipulator.getType() == IType.ITEMS) {
                    return transferToFilters(stack, 4 + 3 + 3, 4 + 3 + 3 + 9);
                } else {
                    return transferToFluidFilters(stack);
                }
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}

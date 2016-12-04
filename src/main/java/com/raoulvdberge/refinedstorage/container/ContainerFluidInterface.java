package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotBase;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFluidInterface extends ContainerBase {
    public ContainerFluidInterface(TileFluidInterface fluidInterface, EntityPlayer player) {
        super(fluidInterface, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotBase(fluidInterface.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotBase(fluidInterface.getIn(), 0, 44, 32));
        addSlotToContainer(new SlotFilterFluid(!fluidInterface.getWorld().isRemote, fluidInterface.getOut(), 0, 116, 32));

        addPlayerInventory(8, 122);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4 + 2) {
                if (!mergeItemStack(stack, 4 + 2, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4 + 1, false)) {
                return mergeItemStackToSpecimen(stack, 5, 6);
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

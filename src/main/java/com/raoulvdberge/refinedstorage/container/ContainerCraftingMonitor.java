package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerCraftingMonitor extends ContainerBase {
    private ICraftingMonitor craftingMonitor;

    public ContainerCraftingMonitor(ICraftingMonitor craftingMonitor, @Nullable TileCraftingMonitor craftingMonitorTile, EntityPlayer player) {
        super(craftingMonitorTile, player);

        this.craftingMonitor = craftingMonitor;

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(craftingMonitor.getFilter(), i, 187, 6 + (18 * i)));
        }

        addPlayerInventory(8, 148);
    }

    public ICraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        return craftingMonitor instanceof WirelessCraftingMonitor;
    }
}

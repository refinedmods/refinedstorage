package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorElements;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ContainerCraftingMonitor extends ContainerBase implements ICraftingMonitorListener {
    private ICraftingMonitor craftingMonitor;
    private boolean addedListener;

    public ContainerCraftingMonitor(ICraftingMonitor craftingMonitor, @Nullable TileCraftingMonitor craftingMonitorTile, EntityPlayer player) {
        super(craftingMonitorTile, player);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        ICraftingManager manager = craftingMonitor.getCraftingManager();
        if (!getPlayer().world.isRemote) {
            if (manager != null && !addedListener) {
                manager.addListener(this);

                this.addedListener = true;
            } else if (manager == null && addedListener) {
                this.addedListener = false;
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        ICraftingManager manager = craftingMonitor.getCraftingManager();
        if (!player.getEntityWorld().isRemote && manager != null && addedListener) {
            manager.removeListener(this);
        }
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
    protected int getDisabledSlotNumber() {
        return craftingMonitor.getSlotId();
    }

    @Override
    public void onAttached() {
        onChanged();
    }

    @Override
    public void onChanged() {
        RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(craftingMonitor), (EntityPlayerMP) getPlayer());
    }
}

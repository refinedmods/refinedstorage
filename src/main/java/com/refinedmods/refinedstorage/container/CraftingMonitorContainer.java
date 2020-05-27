package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class CraftingMonitorContainer extends BaseContainer implements ICraftingMonitorListener {
    private ICraftingMonitor craftingMonitor;
    private boolean addedListener;

    public CraftingMonitorContainer(ContainerType<CraftingMonitorContainer> type, ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorTile craftingMonitorTile, PlayerEntity player, int windowId) {
        super(type, craftingMonitorTile, player, windowId);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!getPlayer().world.isRemote) {
            ICraftingManager manager = craftingMonitor.getCraftingManager();

            if (manager != null && !addedListener) {
                manager.addListener(this);

                this.addedListener = true;
            } else if (manager == null && addedListener) {
                this.addedListener = false;
            }
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        if (!player.getEntityWorld().isRemote) {
            ICraftingManager manager = craftingMonitor.getCraftingManager();

            if (manager != null && addedListener) {
                manager.removeListener(this);
            }
        }
    }

    public ICraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
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
        RS.NETWORK_HANDLER.sendTo((ServerPlayerEntity) getPlayer(), new CraftingMonitorUpdateMessage(craftingMonitor));
    }
}

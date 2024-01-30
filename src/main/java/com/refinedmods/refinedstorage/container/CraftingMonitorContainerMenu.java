package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorSyncTask;
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.ICraftingMonitor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CraftingMonitorContainerMenu extends BaseContainerMenu implements ICraftingMonitorListener {
    private final ICraftingMonitor craftingMonitor;
    private boolean addedListener;

    public CraftingMonitorContainerMenu(MenuType<CraftingMonitorContainerMenu> type, ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorBlockEntity blockEntity, Player player, int windowId) {
        super(type, blockEntity, player, windowId);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!getPlayer().level().isClientSide) {
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
    public void removed(Player player) {
        super.removed(player);

        if (!player.getCommandSenderWorld().isClientSide) {
            craftingMonitor.onClosed(player);

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
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.hasItem()) {
            stack = slot.getItem();

            if (index < 4) {
                if (!moveItemStackTo(stack, 4, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
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
        RS.NETWORK_HANDLER.sendTo((ServerPlayer) getPlayer(), new CraftingMonitorUpdateMessage(
            craftingMonitor.getTasks().stream().map(task -> new CraftingMonitorSyncTask(
                task.getId(),
                task.getRequested(),
                task.getQuantity(),
                task.getStartTime(),
                task.getCompletionPercentage(),
                task.getCraftingMonitorElements()
            )).toList()
        ));
    }
}

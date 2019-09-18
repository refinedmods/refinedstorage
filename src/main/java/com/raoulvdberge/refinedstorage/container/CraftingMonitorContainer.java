package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class CraftingMonitorContainer extends BaseContainer implements ICraftingMonitorListener {
    private ICraftingMonitor craftingMonitor;
    private boolean addedListener;

    public CraftingMonitorContainer(ICraftingMonitor craftingMonitor, @Nullable TileCraftingMonitor craftingMonitorTile, PlayerEntity player, int windowId) {
        super(RSContainers.CRAFTING_MONITOR, craftingMonitorTile, player, windowId);

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
    public void onContainerClosed(PlayerEntity player) {
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
    protected boolean isHeldItemDisabled() {
        return craftingMonitor instanceof WirelessCraftingMonitor;
    }

    @Override
    public void onAttached() {
        onChanged();
    }

    @Override
    public void onChanged() {
        // TODO RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(craftingMonitor), (ServerPlayerEntity) getPlayer());
    }
}

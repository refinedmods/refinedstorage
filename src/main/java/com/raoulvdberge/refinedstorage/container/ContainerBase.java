package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataWatcher;
import invtweaks.api.container.InventoryContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@InventoryContainer(showOptions = false)
public abstract class ContainerBase extends Container {
    @Nullable
    private TileBase tile;
    @Nullable
    private TileDataWatcher listener;
    private EntityPlayer player;

    public ContainerBase(@Nullable TileBase tile, EntityPlayer player) {
        this.tile = tile;
        if (tile != null && player instanceof EntityPlayerMP) {
            listener = new TileDataWatcher((EntityPlayerMP) player, tile.getDataManager());
        }
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Nullable
    public TileBase getTile() {
        return tile;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            if (i == player.inventory.currentItem && isHeldItemDisabled()) {
                addSlotToContainer(new SlotDisabled(player.inventory, id, x, y));
            } else {
                addSlotToContainer(new Slot(player.inventory, id, x, y));
            }

            id++;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));

                id++;
            }
        }
    }

    @Override
    public ItemStack slotClick(int id, int dragType, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (slot instanceof SlotFilter) {
            if (slot.getStack().getItem() == RSItems.FILTER) {
                return super.slotClick(id, dragType, clickType, player);
            }

            if (((SlotFilter) slot).allowsSize()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    slot.putStack(ItemStack.EMPTY);
                } else if (!player.inventory.getItemStack().isEmpty()) {
                    slot.putStack(player.inventory.getItemStack().copy());
                } else if (slot.getHasStack()) {
                    int amount = slot.getStack().getCount();

                    if (dragType == 0) {
                        amount = Math.max(1, amount - 1);
                    } else if (dragType == 1) {
                        amount = Math.min(64, amount + 1);
                    }

                    slot.getStack().setCount(amount);
                }
            } else if (player.inventory.getItemStack().isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                if (player.inventory.getItemStack().getItem() == RSItems.FILTER) {
                    return super.slotClick(id, dragType, clickType, player);
                }

                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotFilterLegacy) {
            if (player.inventory.getItemStack().isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotDisabled) {
            return ItemStack.EMPTY;
        }

        return super.slotClick(id, dragType, clickType, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    protected ItemStack mergeItemStackToFilters(ItemStack stack, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            if (API.instance().getComparer().isEqualNoQuantity(getStackFromSlot(getSlot(i)), stack)) {
                return ItemStack.EMPTY;
            }
        }

        for (int i = begin; i < end; ++i) {
            Slot slot = getSlot(i);

            if (getStackFromSlot(slot).isEmpty() && slot.isItemValid(stack)) {
                slot.putStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
                slot.onSlotChanged();

                if (stack.getItem() == RSItems.FILTER) {
                    stack.setCount(0);
                }

                return ItemStack.EMPTY;
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    private ItemStack getStackFromSlot(Slot slot) {
        ItemStack stackInSlot = slot.getStack();

        if (stackInSlot.isEmpty()) {
            if (slot instanceof SlotFilterFluid) {
                stackInSlot = ((SlotFilterFluid) slot).getRealStack();
            } else if (slot instanceof SlotFilterType) {
                stackInSlot = ((SlotFilterType) slot).getRealStack();
            }
        }

        return stackInSlot;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    protected boolean isHeldItemDisabled() {
        return false;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (listener != null) {
            listener.detectAndSendChanges();
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (listener != null) {
            listener.onClosed();
        }
    }
}

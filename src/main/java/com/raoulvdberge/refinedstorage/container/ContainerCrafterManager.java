package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.gui.grid.IGridDisplay;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerCrafterManager extends ContainerBase {
    public class Listener implements IContainerListener {
        private IContainerListener base;
        private boolean receivedContainerData;

        public Listener(IContainerListener base) {
            this.base = base;
        }

        public EntityPlayerMP getPlayer() {
            return (EntityPlayerMP) base;
        }

        @Override
        public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
            if (receivedContainerData) {
                base.sendAllContents(containerToSend, itemsList);
            }
        }

        @Override
        public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
            if (receivedContainerData) {
                base.sendSlotContents(containerToSend, slotInd, stack);
            }
        }

        public void setReceivedContainerData() {
            receivedContainerData = true;
        }

        @Override
        public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
            base.sendWindowProperty(containerIn, varToUpdate, newValue);
        }

        @Override
        public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
            base.sendAllWindowProperties(containerIn, inventory);
        }
    }

    public class SlotCrafterManager extends SlotItemHandler {
        private boolean visible;

        private SlotCrafterManager(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean visible) {
            super(itemHandler, index, xPosition, yPosition);

            this.visible = visible;
        }

        @Override
        public boolean isEnabled() {
            return yPos >= display.getHeader() && yPos < display.getHeader() + 18 * display.getVisibleRows() && visible;
        }
    }

    private IGridDisplay display;
    private Map<String, Integer> containerData;
    private Map<String, IItemHandlerModifiable> dummyInventories = new HashMap<>();

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(new Listener(listener));
    }

    public List<IContainerListener> getListeners() {
        return listeners;
    }

    public ContainerCrafterManager(TileCrafterManager crafterManager, EntityPlayer player, IGridDisplay display) {
        super(crafterManager, player);

        this.display = display;

        if (!player.world.isRemote) {
            addPlayerInventory(8, display.getYPlayerInventory());

            for (Map.Entry<String, List<IItemHandlerModifiable>> entry : crafterManager.getNode().getNetwork().getCraftingManager().getNamedContainers().entrySet()) {
                for (IItemHandlerModifiable handler : entry.getValue()) {
                    for (int i = 0; i < handler.getSlots(); ++i) {
                        addSlotToContainer(new SlotItemHandler(handler, i, 0, 0));
                    }
                }
            }
        }
    }

    public void initSlots(@Nullable Map<String, Integer> newContainerData) {
        if (newContainerData == null) { // We resized
            if (containerData == null) { // No container data received yet, do nothing..
                return;
            }
        } else {
            containerData = newContainerData; // Received container data

            dummyInventories.clear();
        }

        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        addPlayerInventory(8, display.getYPlayerInventory());

        int y = 19 + 18 - display.getCurrentOffset() * 18;

        for (Map.Entry<String, Integer> entry : containerData.entrySet()) {
            boolean visible = entry.getKey().toLowerCase().contains(display.getSearchFieldText().toLowerCase());

            for (int i = 0; i < entry.getValue(); ++i) {
                IItemHandlerModifiable dummy;

                if (newContainerData == null) { // We're only resizing, get the previous inventory...
                    dummy = dummyInventories.get(entry.getKey() + "," + i);
                } else {
                    dummyInventories.put(entry.getKey() + "," + i, dummy = new ItemHandlerBase(9));
                }

                for (int j = 0; j < 9; ++j) {
                    addSlotToContainer(new SlotCrafterManager(dummy, j, 8 + j * 18, y, visible));
                }

                if (visible) {
                    y += 18;
                }
            }

            if (visible) {
                y += 18;
            }
        }
    }

    public Map<String, Integer> getContainerData() {
        return containerData;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 9 * 4) {
                if (!mergeItemStack(stack, 9 * 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 9 * 4, false)) {
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
}

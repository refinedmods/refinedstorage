package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotCrafterManager;
import com.raoulvdberge.refinedstorage.gui.IResizableDisplay;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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

    private IResizableDisplay display;
    private Map<String, Integer> containerData;
    private Map<String, IItemHandlerModifiable> dummyInventories = new HashMap<>();

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(new Listener(listener));
    }

    public List<IContainerListener> getListeners() {
        return listeners;
    }

    public ContainerCrafterManager(TileCrafterManager crafterManager, EntityPlayer player, IResizableDisplay display) {
        super(crafterManager, player);

        this.display = display;

        if (!player.world.isRemote) {
            addPlayerInventory(8, display.getYPlayerInventory());

            if (crafterManager.getNode().getNetwork() != null) {
                for (Map.Entry<String, List<IItemHandlerModifiable>> entry : crafterManager.getNode().getNetwork().getCraftingManager().getNamedContainers().entrySet()) {
                    for (IItemHandlerModifiable handler : entry.getValue()) {
                        for (int i = 0; i < handler.getSlots(); ++i) {
                            addSlotToContainer(new SlotItemHandler(handler, i, 0, 0));
                        }
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
        int x = 8;

        for (Map.Entry<String, Integer> entry : containerData.entrySet()) {
            // @todo: broken on servers prolly
            boolean visible = I18n.format(entry.getKey()).toLowerCase().contains(display.getSearchFieldText().toLowerCase());

            IItemHandlerModifiable dummy;

            if (newContainerData == null) { // We're only resizing, get the previous inventory...
                dummy = dummyInventories.get(entry.getKey());
            } else {
                dummyInventories.put(entry.getKey(), dummy = new ItemHandlerBase(entry.getValue()));
            }

            for (int slot = 0; slot < entry.getValue(); ++slot) {
                addSlotToContainer(new SlotCrafterManager(dummy, slot, x, y, visible, display));

                if (visible) {
                    x += 18;

                    if ((slot + 1) % 9 == 0) {
                        x = 8;
                        y += 18;
                    }
                }
            }

            if (visible) {
                x = 8;
                y += 18 * 2;
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

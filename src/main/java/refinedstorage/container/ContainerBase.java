package refinedstorage.container;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.slot.SlotDisabled;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.container.slot.SlotSpecimenLegacy;

public abstract class ContainerBase extends Container {
    private EntityPlayer player;

    public ContainerBase(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int id = 0;

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(player.inventory, id, xInventory + i * 18, yInventory + 4 + (3 * 18)));

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
    public ItemStack slotClick(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (slot instanceof SlotSpecimen) {
            if (((SlotSpecimen) slot).isWithSize()) {
                if (slot.getStack() != null) {
                    if (GuiScreen.isShiftKeyDown()) {
                        slot.putStack(null);
                    } else {
                        int amount = slot.getStack().stackSize;

                        if (clickedButton == 0) {
                            amount--;

                            if (amount < 1) {
                                amount = 1;
                            }
                        } else if (clickedButton == 1) {
                            amount++;

                            if (amount > 64) {
                                amount = 64;
                            }
                        }

                        slot.getStack().stackSize = amount;
                    }
                } else if (player.inventory.getItemStack() != null) {
                    int amount = player.inventory.getItemStack().stackSize;

                    if (clickedButton == 1) {
                        amount = 1;
                    }

                    ItemStack toPut = player.inventory.getItemStack().copy();
                    toPut.stackSize = amount;

                    slot.putStack(toPut);
                }
            } else if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotSpecimenLegacy) {
            if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotDisabled) {
            return null;
        }

        return super.slotClick(id, clickedButton, clickType, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        return null;
    }

    public ItemStack mergeItemStackToSpecimen(ItemStack stack, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            if (RefinedStorageUtils.compareStackNoQuantity(getSlot(i).getStack(), stack)) {
                return null;
            }
        }

        for (int i = begin; i < end; ++i) {
            if (!getSlot(i).getHasStack()) {
                getSlot(i).putStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
                getSlot(i).onSlotChanged();

                return null;
            }
        }

        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}

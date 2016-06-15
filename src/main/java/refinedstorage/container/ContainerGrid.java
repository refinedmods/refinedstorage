package refinedstorage.container;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.slot.*;
import refinedstorage.network.MessageGridCraftingShift;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

public class ContainerGrid extends ContainerBase {
    private IGrid grid;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(EntityPlayer player, IGrid grid) {
        super(player);

        this.grid = grid;

        addPlayerInventory(8, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 174 : 126);

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 25;
            int y = 106;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 25;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, player, ((TileGrid) grid).getMatrix(), ((TileGrid) grid).getResult(), (TileGrid) grid, 0, 133 + 4, 120 + 4));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = 106;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotSpecimenLegacy(((TileGrid) grid).getMatrix(), i, x, y, false));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(((TileGrid) grid).getResult(), 0, 116 + 4, 120 + 4));

            addSlotToContainer(new SlotItemHandler(((TileGrid) grid).getPatterns(), 0, 152, 104));
            addSlotToContainer(new SlotOutput(((TileGrid) grid).getPatterns(), 1, 152, 144));
        }
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void detectAndSendChanges() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            if (inventorySlots.get(i) instanceof SlotGridCrafting || inventorySlots.get(i) == craftingResultSlot) {
                for (int j = 0; j < listeners.size(); ++j) {
                    listeners.get(j).sendSlotContents(this, i, inventorySlots.get(i).getStack());
                }
            } else {
                ItemStack current = inventorySlots.get(i).getStack();
                ItemStack cached = inventoryItemStacks.get(i);

                if (!ItemStack.areItemStacksEqual(cached, current)) {
                    cached = current == null ? null : current.copy();

                    inventoryItemStacks.set(i, cached);

                    for (int j = 0; j < listeners.size(); ++j) {
                        listeners.get(j).sendSlotContents(this, i, cached);
                    }
                }
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.worldObj.isRemote && grid instanceof WirelessGrid) {
            ((WirelessGrid) grid).onClose(player);
        }
    }

    @Override
    public ItemStack slotClick(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (player.worldObj.isRemote && slot instanceof SlotGridCraftingResult && grid.getType() == EnumGridType.CRAFTING && slot.getHasStack()) {
            if (GuiScreen.isShiftKeyDown()) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingShift((TileGrid) grid));

                return null;
            }
        }

        return super.slotClick(id, clickedButton, clickType, player);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == craftingResultSlot || slot == patternResultSlot) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (!player.worldObj.isRemote) {
            ItemStack stack = inventorySlots.get(slotIndex).getStack();

            if (stack != null) {
                inventorySlots.get(slotIndex).putStack(grid.onItemPush(player, stack));

                detectAndSendChanges();
            }
        }

        return null;
    }
}

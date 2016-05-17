package refinedstorage.container;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.slot.*;
import refinedstorage.network.MessageGridCraftingShift;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

import java.util.ArrayList;
import java.util.List;

public class ContainerGrid extends ContainerBase {
    private List<Slot> craftingSlots = new ArrayList<Slot>();
    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;
    private IGrid grid;

    public ContainerGrid(EntityPlayer player, IGrid grid) {
        super(player);

        this.grid = grid;

        addPlayerInventory(8, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 174 : 126);

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 25;
            int y = 106;

            for (int i = 0; i < 9; ++i) {
                Slot slot = new Slot(((TileGrid) grid).getCraftingInventory(), i, x, y);

                craftingSlots.add(slot);

                addSlotToContainer(slot);

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 25;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, player, ((TileGrid) grid).getCraftingInventory(), ((TileGrid) grid).getCraftingResultInventory(), (TileGrid) grid, 0, 133 + 4, 120 + 4));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = 106;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotSpecimen(((TileGrid) grid).getCraftingInventory(), i, x, y, false));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(((TileGrid) grid).getCraftingResultInventory(), 0, 116 + 4, 120 + 4));

            addSlotToContainer(new SlotFiltered(((TileGrid) grid).getPatternsInventory(), 0, 152, 104, new BasicItemValidator(RefinedStorageItems.PATTERN)));
            addSlotToContainer(new SlotOutput(((TileGrid) grid).getPatternsInventory(), 1, 152, 144));
        }
    }

    public TileGrid getGrid() {
        return (TileGrid) grid;
    }

    public List<Slot> getCraftingSlots() {
        return craftingSlots;
    }

    /* I'm overriding detectAndSendChanges() here because the default check
     checks if the item stacks are equal, and if so, then it will only send the new slot contents.
     The thing is though, when the grid replaces the slots with new items from the storage
     system, the item stack replaced WILL be the same and thus changes will not be sent!
     That is why we override here to get rid of the check and ALWAYS send slot changes. */
    @Override
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = ((Slot) this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack) this.inventoryItemStacks.get(i);

            itemstack1 = itemstack == null ? null : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);

            for (int j = 0; j < this.crafters.size(); ++j) {
                ((ICrafting) this.crafters.get(j)).sendSlotContents(this, i, itemstack1);
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
    public ItemStack func_184996_a(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (player.worldObj.isRemote && slot instanceof SlotGridCraftingResult && grid.getType() == EnumGridType.CRAFTING && slot.getHasStack()) {
            if (GuiScreen.isShiftKeyDown()) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingShift((TileGrid) grid));

                return null;
            }
        }

        return super.func_184996_a(id, clickedButton, clickType, player);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == craftingResultSlot || slot == patternResultSlot) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }
}

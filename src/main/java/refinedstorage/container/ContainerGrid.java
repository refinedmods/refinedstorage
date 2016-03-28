package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.slot.SlotGridCraftingResult;
import refinedstorage.tile.TileGrid;

import java.util.ArrayList;
import java.util.List;

public class ContainerGrid extends ContainerBase {
    private List<Slot> craftingSlots = new ArrayList<Slot>();

    public ContainerGrid(EntityPlayer player, TileGrid grid) {
        super(player);

        addPlayerInventory(8, grid.getType() == EnumGridType.CRAFTING ? 174 : 126);

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 25;
            int y = 106;

            for (int i = 0; i < 9; ++i) {
                Slot slot = new Slot(grid.getCraftingInventory(), i, x, y);

                craftingSlots.add(slot);

                addSlotToContainer(slot);

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 25;
                }
            }

            addSlotToContainer(new SlotGridCraftingResult(this, player, grid.getCraftingInventory(), grid.getCraftingResultInventory(), grid, 0, 133 + 4, 120 + 4));
        }
    }

    public List<Slot> getCraftingSlots() {
        return craftingSlots;
    }

    // I'm overriding detectAndSendChanges() here because the default check
    // checks if the item stacks are equal, and if so, then it will only send the new slot contents.
    // The thing is though, when the grid replaces the slots with new items from the storage
    // system, the item stack replaced WILL be the same!
    // That's why we override this here to get rid of the check and ALWAYS send slot changes.
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
}

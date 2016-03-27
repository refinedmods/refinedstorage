package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.slot.SlotGridCraftingResult;
import refinedstorage.tile.TileGrid;

import java.util.ArrayList;
import java.util.List;

public class ContainerGrid extends ContainerBase {
    private List<Slot> craftingSlots = new ArrayList<Slot>();

    public ContainerGrid(EntityPlayer player, TileGrid grid) {
        super(player);

        addPlayerInventory(8, grid.getType() == EnumGridType.CRAFTING ? 174 : 108);

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

            addSlotToContainer(new SlotGridCraftingResult(player, grid.getCraftingInventory(), grid.getCraftingResultInventory(), grid, 0, 133 + 4, 120 + 4));
        }
    }

    public List<Slot> getCraftingSlots() {
        return craftingSlots;
    }
}

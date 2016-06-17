package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileInterface;

public class ContainerInterface extends ContainerBase {
    public ContainerInterface(EntityPlayer player, TileInterface tile) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(tile.getExportSpecimenItems(), i, 8 + (18 * i), 54, SlotSpecimen.SPECIMEN_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotOutput(tile.getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 9) {
                if (!mergeItemStack(stack, 9 + 9 + 9 + 4 + 1, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 9, false)) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}

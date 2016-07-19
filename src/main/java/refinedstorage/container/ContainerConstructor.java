package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {
    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(constructor.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotSpecimen(constructor.getFilter(), 0, 80, 20, SlotSpecimen.SPECIMEN_BLOCK));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 4, false)) {
                return mergeItemStackToSpecimen(stack, 4, 4 + 1);
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

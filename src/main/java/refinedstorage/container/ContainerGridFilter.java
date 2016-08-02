package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.inventory.ItemHandlerGridFilter;

public class ContainerGridFilter extends ContainerBase {
    private ItemHandlerGridFilter filter;

    public ContainerGridFilter(EntityPlayer player, ItemStack stack) {
        super(player);

        this.filter = new ItemHandlerGridFilter(stack);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(filter, i, 8 + (i * 18), 20));
        }

        addPlayerInventory(8, 70);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index > 9 - 1) {
                return mergeItemStackToSpecimen(stack, 0, 9);
            }

            return null;
        }

        return stack;
    }
}

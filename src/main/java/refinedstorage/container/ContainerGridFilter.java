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

        int x = 8;
        int y = 20;

        for (int i = 0; i < 9 * 3; ++i) {
            addSlotToContainer(new SlotSpecimen(filter, i, x, y));

            if ((i + 1) % 9 == 0) {
                y += 18;
                x = 8;
            } else {
                x += 18;
            }
        }

        addPlayerInventory(8, 91);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index > (9 * 3) - 1) {
                return mergeItemStackToSpecimen(stack, 0, 9 * 3);
            }

            return null;
        }

        return stack;
    }
}

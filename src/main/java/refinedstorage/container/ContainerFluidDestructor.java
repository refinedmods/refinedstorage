package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimenFluid;
import refinedstorage.tile.TileFluidDestructor;

public class ContainerFluidDestructor extends ContainerBase {
    public ContainerFluidDestructor(TileFluidDestructor fluidDestructor, EntityPlayer player) {
        super(fluidDestructor, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(fluidDestructor.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenFluid(!fluidDestructor.getWorld().isRemote, fluidDestructor.getInventory(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4 + 9, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 4, false)) {
                return mergeItemStackToSpecimen(stack, 4, 4 + 9);
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

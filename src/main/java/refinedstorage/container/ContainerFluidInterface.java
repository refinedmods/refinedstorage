package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimenFluid;
import refinedstorage.tile.TileFluidInterface;

public class ContainerFluidInterface extends ContainerBase {
    public ContainerFluidInterface(TileFluidInterface fluidInterface, EntityPlayer player) {
        super(fluidInterface, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(fluidInterface.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotItemHandler(fluidInterface.getIn(), 0, 44, 32));
        addSlotToContainer(new SlotSpecimenFluid(!fluidInterface.getWorld().isRemote, fluidInterface.getOut(), 0, 116, 32));

        addPlayerInventory(8, 122);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4 + 2) {
                if (!mergeItemStack(stack, 4 + 2, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 4 + 1, false)) {
                return mergeItemStackToSpecimen(stack, 5, 6);
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

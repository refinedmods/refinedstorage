package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.tile.autocrafting.TileCrafter;

public class ContainerCrafter extends ContainerStorage {
    public ContainerCrafter(EntityPlayer player, TileCrafter crafter) {
        super(player);

        for (int i = 0; i < 6; ++i) {
            addSlotToContainer(new SlotItemHandler(crafter.getPatterns(), i, 8, 19 + (i * 18)));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(crafter.getUpgrades(), 6 + i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 144);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 8) {
                if (!mergeItemStack(stack, 6, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 6, false)) {
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

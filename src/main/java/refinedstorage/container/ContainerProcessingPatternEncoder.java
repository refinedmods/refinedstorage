package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileProcessingPatternEncoder;

public class ContainerProcessingPatternEncoder extends ContainerBase {
    public ContainerProcessingPatternEncoder(EntityPlayer player, TileProcessingPatternEncoder processingPatternEncoder) {
        super(player);

        addSlotToContainer(new SlotItemHandler(processingPatternEncoder.getPatterns(), 0, 152, 18));
        addSlotToContainer(new SlotOutput(processingPatternEncoder.getPatterns(), 1, 152, 58));

        int ox = 8;
        int x = ox;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlotToContainer(new SlotSpecimen(processingPatternEncoder.getConfiguration(), i, x, y));

            x += 18;

            if ((i + 1) % 3 == 0) {
                if (i == 8) {
                    ox = 90;
                    x = ox;
                    y = 20;
                } else {
                    x = ox;
                    y += 18;
                }
            }
        }

        addPlayerInventory(8, 90);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && !(slot instanceof SlotSpecimen) && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 2) {
                if (!mergeItemStack(stack, 2 + 18, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 1, false)) {
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

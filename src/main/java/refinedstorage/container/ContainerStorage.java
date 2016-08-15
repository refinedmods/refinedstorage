package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileStorage;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(TileStorage tile, EntityPlayer player) {
        super(tile, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(tile.getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 129);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack() && index >= 8) {
            return mergeItemStackToSpecimen(slot.getStack(), 0, 9);
        }

        return null;
    }
}

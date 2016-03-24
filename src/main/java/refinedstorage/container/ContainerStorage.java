package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import refinedstorage.container.slot.SlotSpecimen;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(EntityPlayer player, IInventory inventory) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(inventory, i, 8 + (18 * i), 20, false));
        }

        addPlayerInventory(8, 129);
    }
}

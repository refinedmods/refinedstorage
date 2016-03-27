package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import refinedstorage.container.slot.SlotSpecimen;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(EntityPlayer player) {
        super(player);
    }

    public ContainerStorage(EntityPlayer player, IInventory inventory) {
        this(player);

        addSpecimenAndPlayerInventorySlots(inventory);
    }

    protected void addSpecimenAndPlayerInventorySlots(IInventory inventory) {
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(inventory, i, 8 + (18 * i), 20, false));
        }

        addPlayerInventory(8, 129);
    }
}

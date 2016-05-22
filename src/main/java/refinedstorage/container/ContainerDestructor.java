package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileDestructor;

public class ContainerDestructor extends ContainerBase {
    public ContainerDestructor(EntityPlayer player, TileDestructor destructor) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(destructor.getInventory(), i, 8 + (18 * i), 20, SlotSpecimen.SPECIMEN_BLOCK));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(destructor.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);
    }
}

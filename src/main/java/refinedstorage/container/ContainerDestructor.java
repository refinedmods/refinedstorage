package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.tile.TileDestructor;

public class ContainerDestructor extends ContainerBase {
    public ContainerDestructor(EntityPlayer player, TileDestructor destructor) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenItemBlock(destructor.getInventory(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 55);
    }
}

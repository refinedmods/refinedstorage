package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.container.slot.UpgradeItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileDestructor;

public class ContainerDestructor extends ContainerBase {
    public ContainerDestructor(EntityPlayer player, TileDestructor destructor) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenItemBlock(destructor.getInventory(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(destructor.getUpgradesInventory(), i, 187, 6 + (i * 18), new UpgradeItemValidator(ItemUpgrade.TYPE_SPEED)));
        }

        addPlayerInventory(8, 55);
    }
}

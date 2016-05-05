package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.container.slot.UpgradeItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {

    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        addSlotToContainer(new SlotSpecimenItemBlock(constructor.getInventory(), 0, 80, 20));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(constructor.getUpgradesInventory(), i, 187, 6 + (i * 18), new UpgradeItemValidator(ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING)));
        }

        addPlayerInventory(8, 55);
    }
}

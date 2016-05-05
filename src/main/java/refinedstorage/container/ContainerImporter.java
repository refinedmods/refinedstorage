package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.container.slot.UpgradeItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileImporter;

public class ContainerImporter extends ContainerBase {
    public ContainerImporter(EntityPlayer player, TileImporter importer) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(importer.getInventory(), i, 8 + (18 * i), 20, false));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(importer.getUpgradesInventory(), i, 187, 6 + (i * 18), new UpgradeItemValidator(ItemUpgrade.TYPE_SPEED)));
        }

        addPlayerInventory(8, 55);
    }
}

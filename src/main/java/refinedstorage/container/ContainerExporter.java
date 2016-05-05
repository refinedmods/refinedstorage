package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.container.slot.UpgradeItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileExporter;

public class ContainerExporter extends ContainerBase {
    public ContainerExporter(EntityPlayer player, TileExporter exporter) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(exporter.getInventory(), i, 8 + (18 * i), 20, false));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(exporter.getUpgradesInventory(), i, 187, 6 + (i * 18), new UpgradeItemValidator(ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING)));
        }

        addPlayerInventory(8, 55);
    }
}

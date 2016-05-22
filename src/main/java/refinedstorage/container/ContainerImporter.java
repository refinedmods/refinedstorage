package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileImporter;

public class ContainerImporter extends ContainerBase {
    public ContainerImporter(EntityPlayer player, TileImporter importer) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(importer.getFilters(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(importer.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);
    }
}

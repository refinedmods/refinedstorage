package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileImporter;

public class ContainerImporter extends ContainerBase {
	public ContainerImporter(EntityPlayer player, TileImporter importer) {
		super(player);

		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new SlotSpecimen(importer, i, 8 + (18 * i), 20));
		}

		addPlayerInventory(8, 55);
	}
}

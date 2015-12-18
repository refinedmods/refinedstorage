package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileExporter;

public class ContainerExporter extends ContainerBase {
	public ContainerExporter(EntityPlayer player, TileExporter exporter) {
		super(player);

		addSlotToContainer(new SlotSpecimen(exporter, 0, 80, 20));

		addPlayerInventory(8, 104);
	}
}

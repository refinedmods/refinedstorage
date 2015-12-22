package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileExporter;

public class ContainerExporter extends ContainerBase
{
	public ContainerExporter(EntityPlayer player, TileExporter exporter)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotSpecimen(exporter, i, 8 + (18 * i), 20));
		}

		addPlayerInventory(8, 55);
	}
}

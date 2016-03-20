package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileExporter;

public class ContainerExporter extends ContainerBase
{
	public ContainerExporter(EntityPlayer player, TileExporter exporter)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotSpecimen(exporter.getInventory(), i, 8 + (18 * i), 20));
		}

		addPlayerInventory(8, 55);
	}
}

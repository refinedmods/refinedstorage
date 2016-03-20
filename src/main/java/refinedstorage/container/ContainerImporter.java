package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileImporter;

public class ContainerImporter extends ContainerBase
{
	public ContainerImporter(EntityPlayer player, TileImporter importer)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotSpecimen(importer.getInventory(), i, 8 + (18 * i), 20, false));
		}

		addPlayerInventory(8, 55);
	}
}

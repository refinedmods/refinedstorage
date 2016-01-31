package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileStorage;

public class ContainerStorage extends ContainerBase
{
	public ContainerStorage(EntityPlayer player, TileStorage storage)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotSpecimen(storage, i, 8 + (18 * i), 20));
		}

		addPlayerInventory(8, 129);
	}
}

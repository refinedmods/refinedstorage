package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import storagecraft.container.slot.SlotOutput;
import storagecraft.tile.TileSolderer;

public class ContainerSolderer extends ContainerBase
{
	public ContainerSolderer(EntityPlayer player, TileSolderer solderer)
	{
		super(player);

		addPlayerInventory(8, 95);

		int x = 44;
		int y = 20;

		for (int i = 0; i < 3; ++i)
		{
			addSlotToContainer(new Slot(solderer, i, x, y));

			y += 18;
		}

		addSlotToContainer(new SlotOutput(solderer, 3, 134, 38));
	}
}

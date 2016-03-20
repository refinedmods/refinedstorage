package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileInterface;

public class ContainerInterface extends ContainerBase
{
	public ContainerInterface(EntityPlayer player, TileInterface tile)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new Slot(tile, i, 8 + (18 * i), 20));
		}

		for (int i = 9; i < 18; ++i)
		{
			addSlotToContainer(new SlotSpecimen(tile, i, 8 + (18 * (i - 9)), 54, true));
		}

		for (int i = 18; i < 27; ++i)
		{
			addSlotToContainer(new SlotOutput(tile, i, 8 + (18 * (i - 18)), 100));
		}

		addPlayerInventory(8, 136);
	}
}

package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.container.slot.SlotSpecimen;
import storagecraft.tile.TileDetector;

public class ContainerDetector extends ContainerBase
{
	public ContainerDetector(EntityPlayer player, TileDetector detector)
	{
		super(player);

		addSlotToContainer(new SlotSpecimen(detector.getInventory(), 0, 107, 20));

		addPlayerInventory(8, 55);
	}
}

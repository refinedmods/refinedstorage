package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerGrid extends ContainerBase
{
	public ContainerGrid(EntityPlayer player)
	{
		super(player);

		addPlayerInventory(8, 108);
	}
}

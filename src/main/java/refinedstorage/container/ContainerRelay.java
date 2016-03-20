package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerRelay extends ContainerBase
{
	public ContainerRelay(EntityPlayer player)
	{
		super(player);

		addPlayerInventory(8, 50);
	}
}

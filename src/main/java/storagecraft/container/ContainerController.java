package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerController extends ContainerBase
{
	public ContainerController(EntityPlayer player)
	{
		super(player);

		addPlayerInventory(8, 108);
	}
}

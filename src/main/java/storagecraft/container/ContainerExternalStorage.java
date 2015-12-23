package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerExternalStorage extends ContainerBase
{
	public ContainerExternalStorage(EntityPlayer player)
	{
		super(player);

		addPlayerInventory(8, 50);
	}
}

package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerStorageProxy extends ContainerBase {
	public ContainerStorageProxy(EntityPlayer player) {
		super(player);

		addPlayerInventory(8, 50);
	}
}

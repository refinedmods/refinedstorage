package storagecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerGrid extends ContainerSC {
	public ContainerGrid(EntityPlayer player) {
		super(player);

		addPlayerInventory(8, 108);
	}
}

package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import storagecraft.StorageCraftItems;
import storagecraft.container.slot.SlotItemFilter;
import storagecraft.tile.TileDrive;

public class ContainerDrive extends ContainerBase {
	public ContainerDrive(EntityPlayer player, TileDrive drive) {
		super(player);

		addPlayerInventory(8, 108);

		int x = 71;
		int y = 20;

		for (int i = 0; i < 8; ++i) {
			addSlotToContainer(new SlotItemFilter(drive, i, x, y, StorageCraftItems.STORAGE_CELL));

			if ((i + 1) % 2 == 0) {
				x = 71;
				y += 18;
			} else {
				x += 18;
			}
		}
	}
}

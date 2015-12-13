package storagecraft.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.tile.TileDrive;

public class SlotDrive extends SlotItemFilter {
	private TileDrive drive;

	public SlotDrive(IInventory inventory, int id, int x, int y, Item item, TileDrive drive) {
		super(inventory, id, x, y, item);

		this.drive = drive;
	}

	@Override
	public boolean isItemValid(ItemStack item) {
		return drive.isConnected() && super.isItemValid(item);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();

		if (drive.isConnected()) {
			drive.getController().syncStorage();
		}
	}
}

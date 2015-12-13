package storagecraft.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.tile.TileDrive;

// @TODO: make special SlotDrive
public class SlotItemFilter extends Slot {
	private Item item;

	private TileDrive dr;

	public SlotItemFilter(IInventory inventory, int id, int x, int y, Item item, TileDrive dr) {
		super(inventory, id, x, y);

		this.item = item;
		this.dr = dr;
	}

	@Override
	public boolean isItemValid(ItemStack item) {
		return dr.isConnected() && item.getItem() == this.item;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();

		dr.getController().getStorage().sync();
	}
}

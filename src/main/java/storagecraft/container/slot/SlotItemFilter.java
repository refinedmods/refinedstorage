package storagecraft.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotItemFilter extends Slot {
	private Item item;

	public SlotItemFilter(IInventory inventory, int id, int x, int y, Item item) {
		super(inventory, id, x, y);

		this.item = item;
	}

	@Override
	public boolean isItemValid(ItemStack item) {
		return item.getItem() == this.item;
	}
}

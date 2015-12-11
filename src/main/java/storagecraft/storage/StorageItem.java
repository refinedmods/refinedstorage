package storagecraft.storage;

import net.minecraft.item.Item;

public class StorageItem {
	private Item type;
	private int quantity;
	private int meta;

	public StorageItem(Item type, int quantity, int meta) {
		this.type = type;
		this.meta = meta;
		this.quantity = quantity;
	}

	public StorageItem(Item type) {
		this.type = type;
		this.meta = 0;
		this.quantity = 1;
	}

	public Item getType() {
		return type;
	}

	public void setType(Item type) {
		this.type = type;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getMeta() {
		return meta;
	}

	public void setMeta(int meta) {
		this.meta = meta;
	}
}

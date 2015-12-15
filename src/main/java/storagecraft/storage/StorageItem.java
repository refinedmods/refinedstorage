package storagecraft.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StorageItem {
	private StorageItemMeta meta;
	private int quantity;

	public StorageItem(StorageItemMeta meta, int quantity) {
		this.meta = meta;
		this.quantity = quantity;
	}

	public StorageItem(Item type, int quantity, int damage, NBTTagCompound tag) {
		this.meta = new StorageItemMeta(type, damage, tag);
		this.quantity = quantity;
	}

	public StorageItem(ItemStack stack) {
		this(stack.getItem(), stack.stackSize, stack.getItemDamage(), stack.stackTagCompound);
	}

	public StorageItemMeta getMeta() {
		return meta;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public StorageItem copy() {
		return copy(quantity);
	}

	public StorageItem copy(int newQuantity) {
		return new StorageItem(meta, newQuantity);
	}

	public ItemStack toItemStack() {
		ItemStack stack = new ItemStack(meta.getType(), quantity, meta.getDamage());

		stack.stackTagCompound = meta.getTag();

		return stack;
	}

	public boolean equals(StorageItem other) {
		return other.getQuantity() == quantity && other.getMeta().equals(meta);
	}
}

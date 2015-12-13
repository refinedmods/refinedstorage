package storagecraft.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StorageItem {
	private Item type;
	private int quantity;
	private int meta;
	private NBTTagCompound tag;

	public StorageItem(Item type, int quantity, int meta, NBTTagCompound tag) {
		this.type = type;
		this.meta = meta;
		this.quantity = quantity;
		this.tag = tag;
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

	public NBTTagCompound getTag() {
		return tag;
	}

	public void setTag(NBTTagCompound tag) {
		this.tag = tag;
	}

	public StorageItem copy() {
		return copy(quantity);
	}

	public StorageItem copy(int newQuantity) {
		return new StorageItem(type, newQuantity, meta, tag);
	}

	public ItemStack toItemStack() {
		ItemStack stack = new ItemStack(type, quantity, meta);

		stack.stackTagCompound = tag;

		return stack;
	}
}

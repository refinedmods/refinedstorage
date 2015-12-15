package storagecraft.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StorageItemMeta {
	private Item type;
	private int damage;
	private NBTTagCompound tag;

	public StorageItemMeta(Item type, int damage, NBTTagCompound tag) {
		this.type = type;
		this.damage = damage;
		this.tag = tag;
	}

	public Item getType() {
		return type;
	}

	public int getDamage() {
		return damage;
	}

	public NBTTagCompound getTag() {
		return tag;
	}

	public boolean equals(StorageItemMeta meta) {
		if (tag != null && !tag.equals(meta.getTag())) {
			return false;
		}

		return type == meta.getType() && damage == meta.getDamage();
	}

	public boolean equals(ItemStack stack) {
		if (tag != null && !tag.equals(stack.stackTagCompound)) {
			return false;
		}

		return type == stack.getItem() && damage == stack.getItemDamage();
	}
}

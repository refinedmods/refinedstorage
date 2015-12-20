package storagecraft.storage;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.util.InventoryUtils;

public class StorageItem {
	private Item type;
	private int quantity;
	private int damage;
	private NBTTagCompound tag;
	@SideOnly(Side.CLIENT)
	private int id;

	public StorageItem(ByteBuf buf) {
		this.id = buf.readInt();
		this.type = Item.getItemById(buf.readInt());
		this.quantity = buf.readInt();
		this.damage = buf.readInt();
		this.tag = buf.readBoolean() ? ByteBufUtils.readTag(buf) : null;
	}

	public StorageItem(Item type, int quantity, int damage, NBTTagCompound tag) {
		this.type = type;
		this.quantity = quantity;
		this.damage = damage;
		this.tag = tag;
	}

	public StorageItem(Item type, int quantity, int damage, NBTTagCompound tag, int id) {
		this(type, quantity, damage, tag);

		this.id = id;
	}

	public StorageItem(ItemStack stack) {
		this(stack.getItem(), stack.stackSize, stack.getItemDamage(), stack.stackTagCompound);
	}

	public void toBytes(ByteBuf buf, int id) {
		buf.writeInt(id);
		buf.writeInt(Item.getIdFromItem(type));
		buf.writeInt(quantity);
		buf.writeInt(damage);
		buf.writeBoolean(tag != null);

		if (tag != null) {
			ByteBufUtils.writeTag(buf, tag);
		}
	}

	public Item getType() {
		return type;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public NBTTagCompound getTag() {
		return tag;
	}

	public void setTag(NBTTagCompound tag) {
		this.tag = tag;
	}

	@SideOnly(Side.CLIENT)
	public int getId() {
		return id;
	}

	public StorageItem copy() {
		return copy(quantity);
	}

	public StorageItem copy(int newQuantity) {
		return new StorageItem(type, newQuantity, damage, tag);
	}

	public ItemStack toItemStack() {
		ItemStack stack = new ItemStack(type, quantity, damage);

		stack.stackTagCompound = tag;

		return stack;
	}

	public boolean compare(StorageItem other, int flags) {
		if ((flags & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT) {
			if (tag != null && !tag.equals(other.getTag())) {
				return false;
			}
		}

		if ((flags & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE) {
			if (damage != other.getDamage()) {
				return false;
			}
		}

		if ((flags & InventoryUtils.COMPARE_QUANTITY) == InventoryUtils.COMPARE_QUANTITY) {
			if (quantity != other.getQuantity()) {
				return false;
			}
		}

		return type == other.getType();
	}

	public boolean compare(ItemStack stack, int flags) {
		if ((flags & InventoryUtils.COMPARE_NBT) == InventoryUtils.COMPARE_NBT) {
			if (tag != null && !tag.equals(stack.stackTagCompound)) {
				return false;
			}
		}

		if ((flags & InventoryUtils.COMPARE_DAMAGE) == InventoryUtils.COMPARE_DAMAGE) {
			if (damage != stack.getItemDamage()) {
				return false;
			}
		}

		if ((flags & InventoryUtils.COMPARE_QUANTITY) == InventoryUtils.COMPARE_QUANTITY) {
			if (quantity != stack.stackSize) {
				return false;
			}
		}

		return type == stack.getItem();
	}

	public boolean compareNoQuantity(StorageItem other) {
		return compare(other, InventoryUtils.COMPARE_NBT | InventoryUtils.COMPARE_DAMAGE);
	}

	public boolean compareNoQuantity(ItemStack stack) {
		return compare(stack, InventoryUtils.COMPARE_NBT | InventoryUtils.COMPARE_DAMAGE);
	}
}

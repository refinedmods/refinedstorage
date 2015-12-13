package storagecraft.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import storagecraft.storage.StorageItem;

public class ItemStorageCell extends ItemSC {
	// @TODO: Different types of storage cells
	public static final int MAX_STORED = 64;

	public ItemStorageCell() {
		super("storageCell");

		setMaxStackSize(1);
	}

	// @TODO: clean everythin up
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);

		stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setTag("Items", new NBTTagList());
	}

	public static List<StorageItem> getStoredItems(ItemStack cell) {
		List<StorageItem> items = new ArrayList<StorageItem>();

		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag("Items");

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			Item type = Item.getItemById(tag.getInteger("Type"));
			int quantity = tag.getInteger("Quantity");
			int meta = tag.getInteger("Meta");

			items.add(new StorageItem(type, quantity, meta));
		}

		return items;
	}

	public static int getQuantityStored(ItemStack cell) {
		int quantity = 0;

		for (StorageItem item : getStoredItems(cell)) {
			quantity += item.getQuantity();
		}

		return quantity;
	}

	// @TODO: store quantity of items on itemStack itself (for speed, and displaying)
	public static void store(ItemStack cell, ItemStack stack) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag("Items");

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			Item type = Item.getItemById(tag.getInteger("Type"));
			int quantity = tag.getInteger("Quantity");
			int meta = tag.getInteger("Meta");

			if (type == stack.getItem() && meta == stack.getItemDamage()) {
				tag.setInteger("Quantity", quantity + stack.stackSize);

				return;
			}
		}

		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger("Type", Item.getIdFromItem(stack.getItem()));
		tag.setInteger("Quantity", stack.stackSize);
		tag.setInteger("Meta", stack.getItemDamage());

		list.appendTag(tag);
	}

	public static int take(ItemStack cell, Item type, int quantity, int meta) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag("Items");

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			Item typeInCell = Item.getItemById(tag.getInteger("Type"));
			int quantityInCell = tag.getInteger("Quantity");
			int metaInCell = tag.getInteger("Meta");

			if (typeInCell == type && metaInCell == meta) {
				if (quantity > quantityInCell) {
					quantity = quantityInCell;
				}

				tag.setInteger("Quantity", quantityInCell - quantity);

				if (quantityInCell - quantity == 0) {
					list.removeTag(i);
				}

				return quantity;
			}
		}

		return 0;
	}

	public static boolean hasSpace(ItemStack cell, ItemStack stack) {
		return (getQuantityStored(cell) + stack.stackSize) <= MAX_STORED;
	}
}

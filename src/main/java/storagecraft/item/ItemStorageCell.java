package storagecraft.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.storage.StorageItem;

public class ItemStorageCell extends ItemSC {
	public static final String NBT_ITEMS = "Items";
	public static final String NBT_STORED = "Stored";

	public static final String NBT_ITEM_TYPE = "Type";
	public static final String NBT_ITEM_QUANTITY = "Quantity";
	public static final String NBT_ITEM_META = "Meta";

	public ItemStorageCell() {
		super("storageCell");

		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; ++i) {
			ItemStack cell = new ItemStack(item, 1, i);

			init(cell);

			list.add(cell);
		}
	}

	@Override
	public void addInformation(ItemStack cell, EntityPlayer player, List list, boolean b) {
		list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storageCellStored"), getStored(cell), getCapacity(cell)));
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);

		init(stack);
	}

	private static StorageItem createItemFromNBT(NBTTagCompound tag) {
		return new StorageItem(Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)), tag.getInteger(NBT_ITEM_QUANTITY), tag.getInteger(NBT_ITEM_META));
	}

	public static List<StorageItem> getStoredItems(ItemStack cell) {
		List<StorageItem> items = new ArrayList<StorageItem>();

		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		for (int i = 0; i < list.tagCount(); ++i) {
			items.add(createItemFromNBT(list.getCompoundTagAt(i)));
		}

		return items;
	}

	public static void init(ItemStack cell) {
		cell.stackTagCompound = new NBTTagCompound();
		cell.stackTagCompound.setTag(NBT_ITEMS, new NBTTagList());
		cell.stackTagCompound.setInteger(NBT_STORED, 0);
	}

	public static void push(ItemStack cell, ItemStack stack) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		cell.stackTagCompound.setInteger(NBT_STORED, getStored(cell) + stack.stackSize);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			StorageItem item = createItemFromNBT(tag);

			if (item.getType() == stack.getItem() && item.getMeta() == stack.getItemDamage()) {
				tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity() + stack.stackSize);

				return;
			}
		}

		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
		tag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
		tag.setInteger(NBT_ITEM_META, stack.getItemDamage());

		list.appendTag(tag);
	}

	public static int take(ItemStack cell, Item type, int quantity, int meta) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			StorageItem item = createItemFromNBT(tag);

			if (item.getType() == type && item.getMeta() == meta) {
				if (quantity > item.getQuantity()) {
					quantity = item.getQuantity();
				}

				tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity() - quantity);

				if (item.getQuantity() - quantity == 0) {
					list.removeTag(i);
				}

				cell.stackTagCompound.setInteger(NBT_STORED, getStored(cell) - quantity);

				return quantity;
			}
		}

		return 0;
	}

	public static boolean hasSpace(ItemStack cell, ItemStack stack) {
		return (getStored(cell) + stack.stackSize) <= getCapacity(cell);
	}

	public static int getStored(ItemStack cell) {
		return cell.stackTagCompound.getInteger(NBT_STORED);
	}

	public static int getCapacity(ItemStack cell) {
		switch (cell.getItemDamage()) {
			case 0:
				return 1000;
			case 1:
				return 4000;
			case 2:
				return 16000;
			case 3:
				return 64000;
		}

		return 0;
	}
}

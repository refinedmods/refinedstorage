package storagecraft.storage;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CellStorage implements IStorage {
	public static final String NBT_ITEMS = "Items";
	public static final String NBT_STORED = "Stored";

	public static final String NBT_ITEM_TYPE = "Type";
	public static final String NBT_ITEM_QUANTITY = "Quantity";
	public static final String NBT_ITEM_META = "Meta";
	public static final String NBT_ITEM_NBT = "NBT";

	private ItemStack cell;

	public CellStorage(ItemStack cell) {
		this.cell = cell;
	}

	private StorageItem createItemFromNBT(NBTTagCompound tag) {
		return new StorageItem(Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)), tag.getInteger(NBT_ITEM_QUANTITY), tag.getInteger(NBT_ITEM_META), tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null);
	}

	@Override
	public void addItems(List<StorageItem> items) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		for (int i = 0; i < list.tagCount(); ++i) {
			items.add(createItemFromNBT(list.getCompoundTagAt(i)));
		}
	}

	@Override
	public void push(ItemStack stack) {
		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		cell.stackTagCompound.setInteger(NBT_STORED, getStored(cell) + stack.stackSize);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			StorageItem item = createItemFromNBT(tag);

			if (item.equalsIgnoreQuantity(stack)) {
				tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity() + stack.stackSize);

				return;
			}
		}

		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
		tag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
		tag.setInteger(NBT_ITEM_META, stack.getItemDamage());

		if (stack.stackTagCompound != null) {
			tag.setTag(NBT_ITEM_NBT, stack.stackTagCompound);
		}

		list.appendTag(tag);
	}

	@Override
	public int take(ItemStack stack) {
		int quantity = stack.stackSize;

		NBTTagList list = (NBTTagList) cell.stackTagCompound.getTag(NBT_ITEMS);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			StorageItem item = createItemFromNBT(tag);

			if (item.equalsIgnoreQuantity(stack)) {
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

	@Override
	public boolean canPush(ItemStack stack) {
		if (getCapacity(cell) == -1) {
			return true;
		}

		return (getStored(cell) + stack.stackSize) <= getCapacity(cell);
	}

	public static int getStored(ItemStack cell) {
		return cell.stackTagCompound.getInteger(CellStorage.NBT_STORED);
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
			case 4:
				return -1;
		}

		return 0;
	}

	public static ItemStack init(ItemStack cell) {
		cell.stackTagCompound = new NBTTagCompound();
		cell.stackTagCompound.setTag(CellStorage.NBT_ITEMS, new NBTTagList());
		cell.stackTagCompound.setInteger(CellStorage.NBT_STORED, 0);

		return cell;
	}
}

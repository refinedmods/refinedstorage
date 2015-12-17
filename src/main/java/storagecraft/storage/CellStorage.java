package storagecraft.storage;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import storagecraft.item.ItemStorageCell;

public class CellStorage implements IStorage {
	public static final String NBT_ITEMS = "Items";
	public static final String NBT_STORED = "Stored";

	public static final String NBT_ITEM_TYPE = "Type";
	public static final String NBT_ITEM_QUANTITY = "Quantity";
	public static final String NBT_ITEM_DAMAGE = "Damage";
	public static final String NBT_ITEM_NBT = "NBT";

	private ItemStack cell;

	public CellStorage(ItemStack cell) {
		this.cell = cell;
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

		cell.stackTagCompound.setInteger(NBT_STORED, ItemStorageCell.getStored(cell) + stack.stackSize);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			StorageItem item = createItemFromNBT(tag);

			if (item.getMeta().equals(stack)) {
				tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity() + stack.stackSize);

				return;
			}
		}

		NBTTagCompound tag = new NBTTagCompound();

		tag.setInteger(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
		tag.setInteger(NBT_ITEM_QUANTITY, stack.stackSize);
		tag.setInteger(NBT_ITEM_DAMAGE, stack.getItemDamage());

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

			if (item.getMeta().equals(stack)) {
				if (quantity > item.getQuantity()) {
					quantity = item.getQuantity();
				}

				tag.setInteger(NBT_ITEM_QUANTITY, item.getQuantity() - quantity);

				if (item.getQuantity() - quantity == 0) {
					list.removeTag(i);
				}

				cell.stackTagCompound.setInteger(NBT_STORED, ItemStorageCell.getStored(cell) - quantity);

				return quantity;
			}
		}

		return 0;
	}

	@Override
	public boolean canPush(ItemStack stack) {
		if (ItemStorageCell.getCapacity(cell) == -1) {
			return true;
		}

		return (ItemStorageCell.getStored(cell) + stack.stackSize) <= ItemStorageCell.getCapacity(cell);
	}

	private StorageItem createItemFromNBT(NBTTagCompound tag) {
		return new StorageItem(Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)), tag.getInteger(NBT_ITEM_QUANTITY), tag.getInteger(NBT_ITEM_DAMAGE), tag.hasKey(NBT_ITEM_NBT) ? ((NBTTagCompound) tag.getTag(NBT_ITEM_NBT)) : null);
	}
}

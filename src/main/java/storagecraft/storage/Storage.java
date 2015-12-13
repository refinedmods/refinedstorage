package storagecraft.storage;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.item.ItemStorageCell;

public class Storage {
	private IStorageCellProvider provider;
	private List<StorageItem> items = new ArrayList<StorageItem>();

	public Storage(IStorageCellProvider provider) {
		this.provider = provider;

		sync();
	}

	public List<StorageItem> getItems() {
		return items;
	}

	public void sync() {
		items.clear();

		for (ItemStack cell : provider.getStorageCells()) {
			items.addAll(ItemStorageCell.getStoredItems(cell));
		}
	}

	public boolean push(ItemStack stack) {
		ItemStack cellWithSpace = null;

		for (ItemStack cell : provider.getStorageCells()) {
			if (ItemStorageCell.hasSpace(cell, stack)) {
				cellWithSpace = cell;

				break;
			}
		}

		if (cellWithSpace == null) {
			return false;
		}

		ItemStorageCell.store(cellWithSpace, stack);

		sync();

		return true;
	}

	public ItemStack take(Item type, int quantity, int meta) {
		int took = 0;

		for (ItemStack cell : provider.getStorageCells()) {
			took += ItemStorageCell.take(cell, type, quantity, meta);

			if (took == quantity) {
				break;
			}
		}

		sync();

		return new ItemStack(type, took, meta);
	}

	public void fromBytes(ByteBuf buf) {
		items.clear();

		int size = buf.readInt();

		for (int i = 0; i < size; ++i) {
			Item type = Item.getItemById(buf.readInt());
			int quantity = buf.readInt();
			int meta = buf.readInt();

			items.add(new StorageItem(type, quantity, meta));
		}
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(items.size());

		for (StorageItem item : items) {
			buf.writeInt(Item.getIdFromItem(item.getType()));
			buf.writeInt(item.getQuantity());
			buf.writeInt(item.getMeta());
		}
	}
}

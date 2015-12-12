package storagecraft.storage;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Storage {
	private List<StorageItem> items = new ArrayList<StorageItem>();

	public List<StorageItem> all() {
		return items;
	}

	public StorageItem get(ItemStack stack) {
		for (StorageItem item : items) {
			if (item.getType() == stack.getItem() && item.getMeta() == stack.getItemDamage()) {
				return item;
			}
		}

		return null;
	}

	public boolean has(ItemStack stack) {
		return get(stack) != null;
	}

	public void push(ItemStack stack) {
		if (has(stack)) {
			StorageItem item = get(stack);

			item.setQuantity(item.getQuantity() + stack.stackSize);
		} else {
			items.add(new StorageItem(stack.getItem(), stack.stackSize, stack.getItemDamage()));
		}
	}

	public ItemStack take(Item type, int quantity, int meta) {
		for (StorageItem item : items) {
			if (item.getType() == type && item.getMeta() == meta) {
				if (item.getQuantity() < quantity) {
					quantity = item.getQuantity();
				}

				item.setQuantity(item.getQuantity() - quantity);

				if (item.getQuantity() == 0) {
					items.remove(item);
				}

				return new ItemStack(type, quantity, meta);
			}
		}

		return null;
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

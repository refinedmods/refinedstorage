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

	public boolean take(ItemStack stack) {
		if (has(stack)) {
			StorageItem item = get(stack);

			if (item.getQuantity() < stack.stackSize) {
				return false;
			}

			item.setQuantity(item.getQuantity() - stack.stackSize);

			if (item.getQuantity() == 0) {
				items.remove(get(stack));
			}

			return true;
		}

		return false;
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

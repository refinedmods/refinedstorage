package storagecraft.storage;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IStorage {
	public List<StorageItem> getAll();

	public void push(ItemStack stack);

	public int take(Item type, int quantity, int meta);

	public boolean canPush(ItemStack stack);
}

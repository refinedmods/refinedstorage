package storagecraft.storage;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IStorage {
	public List<StorageItem> getAll();

	public void push(ItemStack stack);

	public int take(ItemStack stack);

	public boolean canPush(ItemStack stack);
}

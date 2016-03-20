package refinedstorage.storage;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IStorage
{
	public void addItems(List<StorageItem> items);

	public void push(ItemStack stack);

	public ItemStack take(ItemStack stack, int flags);

	public boolean canPush(ItemStack stack);

	public int getPriority();
}

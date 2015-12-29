package storagecraft.storage;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IStorage
{
	public void addItems(List<StorageItem> items);

	public void push(ItemStack stack);

	public ItemStack take(ItemStack stack, int flags);

	public boolean canPush(ItemStack stack);
}

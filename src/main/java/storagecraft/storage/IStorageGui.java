package storagecraft.storage;

import net.minecraft.inventory.IInventory;
import storagecraft.tile.IRedstoneModeSetting;

public interface IStorageGui
{
	public interface IPriorityHandler
	{
		public void onPriorityChanged(int priority);
	}

	public String getName();

	public IStorage getStorage();

	public IInventory getInventory();

	public IRedstoneModeSetting getRedstoneModeSetting();

	public int getStored();

	public int getCapacity();

	public IPriorityHandler getPriorityHandler();
}

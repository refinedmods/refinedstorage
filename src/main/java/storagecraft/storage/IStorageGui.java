package storagecraft.storage;

import net.minecraft.inventory.IInventory;
import storagecraft.tile.ICompareSetting;
import storagecraft.tile.IRedstoneModeSetting;
import storagecraft.tile.IModeSetting;

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

	public ICompareSetting getCompareSetting();

	public IModeSetting getWhitelistBlacklistSetting();

	public int getStored();

	public int getCapacity();

	public IPriorityHandler getPriorityHandler();
}

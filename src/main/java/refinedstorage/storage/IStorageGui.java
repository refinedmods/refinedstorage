package refinedstorage.storage;

import net.minecraft.inventory.IInventory;
import refinedstorage.tile.settings.ICompareSetting;
import refinedstorage.tile.settings.IModeSetting;
import refinedstorage.tile.settings.IRedstoneModeSetting;

public interface IStorageGui
{
	public interface IPriorityHandler
	{
		public void onPriorityChanged(int priority);
	}

	public String getName();

	public int getPriority();

	public IInventory getInventory();

	public IRedstoneModeSetting getRedstoneModeSetting();

	public ICompareSetting getCompareSetting();

	public IModeSetting getWhitelistBlacklistSetting();

	public int getStored();

	public int getCapacity();

	public IPriorityHandler getPriorityHandler();
}

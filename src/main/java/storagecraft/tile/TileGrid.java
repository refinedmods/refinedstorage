package storagecraft.tile;

import net.minecraft.nbt.NBTTagCompound;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileGrid extends TileMachine
{
	private InventorySimple craftingInventory = new InventorySimple("crafting", 10);

	@Override
	public int getEnergyUsage()
	{
		return 5;
	}

	@Override
	public void updateMachine()
	{
	}

	public int getType()
	{
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public boolean isCrafting()
	{
		return getType() == 1;
	}

	public InventorySimple getCraftingInventory()
	{
		return craftingInventory;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(craftingInventory, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(craftingInventory, nbt);
	}
}

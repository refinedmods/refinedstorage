package storagecraft.tile;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.container.ContainerGridCrafting;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileGrid extends TileMachine
{
	private InventoryCrafting craftingMatrix = new InventoryCrafting(new ContainerGridCrafting(this), 3, 3);
	private InventorySimple craftingResult = new InventorySimple("craftingResult", 1);

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

	public InventoryCrafting getCraftingMatrix()
	{
		return craftingMatrix;
	}

	public void onCraftingMatrixChanged()
	{
		craftingResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingMatrix, worldObj));
	}

	public InventorySimple getCraftingResult()
	{
		return craftingResult;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(craftingMatrix, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(craftingMatrix, nbt);
	}
}

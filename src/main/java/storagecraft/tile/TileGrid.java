package storagecraft.tile;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.StorageCraft;
import storagecraft.container.ContainerGridCrafting;
import storagecraft.inventory.InventorySimple;
import storagecraft.network.MessageGridCraftingUpdate;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileGrid extends TileMachine
{
	private ContainerGridCrafting craftingMatrixContainer = new ContainerGridCrafting(this);
	private InventoryCrafting craftingMatrix = new InventoryCrafting(craftingMatrixContainer, 3, 3);
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

	public void onCrafted(ItemStack[] matrixSlots)
	{
		if (isConnected() && !worldObj.isRemote)
		{
			for (int i = 0; i < craftingMatrix.getSizeInventory(); ++i)
			{
				ItemStack slot = craftingMatrix.getStackInSlot(i);

				if (slot == null && matrixSlots[i] != null)
				{
					for (StorageItem item : getController().getItems())
					{
						if (item.compareNoQuantity(matrixSlots[i].copy()))
						{
							craftingMatrix.setInventorySlotContents(i, getController().take(matrixSlots[i].copy()));

							break;
						}
					}
				}
			}

			onCraftingMatrixChanged();

			// @TODO: HACK!
			StorageCraft.NETWORK.sendToAll(new MessageGridCraftingUpdate(this));
		}
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

package storagecraft.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import storagecraft.StorageCraft;
import storagecraft.block.BlockGrid;
import storagecraft.block.EnumGridType;
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

	public EnumGridType getType()
	{
		return (EnumGridType) worldObj.getBlockState(pos).getValue(BlockGrid.TYPE);
	}

	public InventoryCrafting getCraftingMatrix()
	{
		return craftingMatrix;
	}

	public void onCraftingMatrixChanged()
	{
		markDirty();

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
			TargetPoint target = new TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), UPDATE_RANGE);

			StorageCraft.NETWORK.sendToAllAround(new MessageGridCraftingUpdate(this), target);
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

	@Override
	public IInventory getDroppedInventory()
	{
		return craftingMatrix;
	}
}

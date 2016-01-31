package storagecraft.tile;

import io.netty.buffer.ByteBuf;
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
	public static final String NBT_SORTING_DIRECTION = "SortingDirection";
	public static final String NBT_SORTING_TYPE = "SortingType";

	public static final int SORTING_DIRECTION_ASCENDING = 0;
	public static final int SORTING_DIRECTION_DESCENDING = 1;

	public static final int SORTING_TYPE_QUANTITY = 0;
	public static final int SORTING_TYPE_NAME = 1;

	private ContainerGridCrafting craftingMatrixContainer = new ContainerGridCrafting(this);
	private InventoryCrafting craftingMatrix = new InventoryCrafting(craftingMatrixContainer, 3, 3);
	private InventorySimple craftingResult = new InventorySimple("crafting_result", 1);

	private int sortingDirection = 0;
	private int sortingType = 0;

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

	public int getSortingDirection()
	{
		return sortingDirection;
	}

	public void setSortingDirection(int sortingDirection)
	{
		this.sortingDirection = sortingDirection;
	}

	public int getSortingType()
	{
		return sortingType;
	}

	public void setSortingType(int sortingType)
	{
		this.sortingType = sortingType;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(craftingMatrix, nbt);

		if (nbt.hasKey(NBT_SORTING_DIRECTION))
		{
			sortingDirection = nbt.getInteger(NBT_SORTING_DIRECTION);
		}

		if (nbt.hasKey(NBT_SORTING_TYPE))
		{
			sortingType = nbt.getInteger(NBT_SORTING_TYPE);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(craftingMatrix, nbt);

		nbt.setInteger(NBT_SORTING_DIRECTION, sortingDirection);
		nbt.setInteger(NBT_SORTING_TYPE, sortingType);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(sortingDirection);
		buf.writeInt(sortingType);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		sortingDirection = buf.readInt();
		sortingType = buf.readInt();
	}

	@Override
	public IInventory getDroppedInventory()
	{
		return craftingMatrix;
	}
}

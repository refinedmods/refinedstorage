package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.BlockGrid;
import refinedstorage.block.EnumGridType;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.network.MessageGridCraftingUpdate;
import refinedstorage.storage.StorageItem;
import refinedstorage.util.InventoryUtils;

public class TileGrid extends TileMachine
{
	public static final String NBT_SORTING_DIRECTION = "SortingDirection";
	public static final String NBT_SORTING_TYPE = "SortingType";

	public static final int SORTING_DIRECTION_ASCENDING = 0;
	public static final int SORTING_DIRECTION_DESCENDING = 1;

	public static final int SORTING_TYPE_QUANTITY = 0;
	public static final int SORTING_TYPE_NAME = 1;

	private Container craftingContainer = new Container()
	{
		@Override
		public boolean canInteractWith(EntityPlayer player)
		{
			return false;
		}

		@Override
		public void onCraftMatrixChanged(IInventory inventory)
		{
			onCraftingMatrixChanged();
		}
	};
	private InventoryCrafting craftingInventory = new InventoryCrafting(craftingContainer, 3, 3);
	private InventorySimple craftingResultInventory = new InventorySimple("crafting_result", 1);

	private int sortingDirection = 0;
	private int sortingType = 0;

	@Override
	public int getEnergyUsage()
	{
		return 4;
	}

	@Override
	public void updateMachine()
	{
	}

	public EnumGridType getType()
	{
		if (worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.GRID)
		{
			return (EnumGridType) worldObj.getBlockState(pos).getValue(BlockGrid.TYPE);
		}

		return EnumGridType.NORMAL;
	}

	public InventoryCrafting getCraftingInventory()
	{
		return craftingInventory;
	}

	public InventorySimple getCraftingResultInventory()
	{
		return craftingResultInventory;
	}

	public void onCraftingMatrixChanged()
	{
		markDirty();

		craftingResultInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingInventory, worldObj));
	}

	public void onCrafted(ItemStack[] matrixSlots)
	{
		if (isConnected() && !worldObj.isRemote)
		{
			for (int i = 0; i < craftingInventory.getSizeInventory(); ++i)
			{
				ItemStack slot = craftingInventory.getStackInSlot(i);

				if (slot == null && matrixSlots[i] != null)
				{
					for (StorageItem item : getController().getItems())
					{
						if (item.compareNoQuantity(matrixSlots[i].copy()))
						{
							craftingInventory.setInventorySlotContents(i, getController().take(matrixSlots[i].copy()));

							break;
						}
					}
				}
			}

			onCraftingMatrixChanged();

			TargetPoint target = new TargetPoint(worldObj.provider.getDimensionType().getId(), pos.getX(), pos.getY(), pos.getZ(), UPDATE_RANGE);

			RefinedStorage.NETWORK.sendToAllAround(new MessageGridCraftingUpdate(this), target);
		}
	}

	public int getSortingDirection()
	{
		return sortingDirection;
	}

	public void setSortingDirection(int sortingDirection)
	{
		markDirty();

		this.sortingDirection = sortingDirection;
	}

	public int getSortingType()
	{
		return sortingType;
	}

	public void setSortingType(int sortingType)
	{
		markDirty();

		this.sortingType = sortingType;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(craftingInventory, 0, nbt);

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

		InventoryUtils.saveInventory(craftingInventory, 0, nbt);

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
		if (getType() == EnumGridType.CRAFTING)
		{
			return craftingInventory;
		}

		return null;
	}
}

package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftItems;
import storagecraft.block.BlockGrid;
import storagecraft.block.EnumGridType;
import storagecraft.inventory.InventorySimple;
import storagecraft.item.ItemPattern;
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

	private Container patternCraftingContainer = new Container()
	{
		@Override
		public boolean canInteractWith(EntityPlayer player)
		{
			return false;
		}

		@Override
		public void onCraftMatrixChanged(IInventory inventory)
		{
			onPatternCraftingMatrixChanged();
		}
	};
	private InventoryCrafting patternCraftingInventory = new InventoryCrafting(patternCraftingContainer, 3, 3);
	private InventorySimple patternCraftingResultInventory = new InventorySimple("pattern_crafting_result", 1);
	private InventorySimple patternInventory = new InventorySimple("pattern", 2, this);

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

			// @TODO: HACK!
			TargetPoint target = new TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), UPDATE_RANGE);

			StorageCraft.NETWORK.sendToAllAround(new MessageGridCraftingUpdate(this), target);
		}
	}

	public InventoryCrafting getPatternCraftingInventory()
	{
		return patternCraftingInventory;
	}

	public InventorySimple getPatternCraftingResultInventory()
	{
		return patternCraftingResultInventory;
	}

	public InventorySimple getPatternInventory()
	{
		return patternInventory;
	}

	public void onPatternCraftingMatrixChanged()
	{
		markDirty();

		patternCraftingResultInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(patternCraftingInventory, worldObj));
	}

	public void onPatternCreate()
	{
		ItemStack result = patternCraftingResultInventory.getStackInSlot(0);

		if (result != null && patternInventory.getStackInSlot(0) != null && patternInventory.getStackInSlot(0).stackSize > 0 && patternInventory.getStackInSlot(1) == null)
		{
			ItemStack pattern = new ItemStack(StorageCraftItems.PATTERN);

			for (int i = 0; i < 9; ++i)
			{
				ItemStack slot = patternCraftingInventory.getStackInSlot(i);

				if (slot != null)
				{
					ItemPattern.setPattern(pattern, slot, i);
				}
			}

			patternInventory.decrStackSize(0, 1);
			patternInventory.setInventorySlotContents(1, pattern);
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

		InventoryUtils.restoreInventory(patternCraftingInventory, 1, nbt);
		InventoryUtils.restoreInventory(patternInventory, 2, nbt);

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

		InventoryUtils.saveInventory(patternCraftingInventory, 1, nbt);
		InventoryUtils.saveInventory(patternInventory, 2, nbt);

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
		else if (getType() == EnumGridType.PATTERN)
		{
			return patternInventory;
		}

		return null;
	}
}

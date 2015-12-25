package storagecraft.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class InventoryUtils
{
	public static final String NBT_INVENTORY = "Inventory";
	public static final String NBT_SLOT = "Slot";

	public static final int COMPARE_DAMAGE = 1;
	public static final int COMPARE_NBT = 2;
	public static final int COMPARE_QUANTITY = 4;

	public static void saveInventory(IInventory inventory, NBTTagCompound nbt)
	{
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			if (inventory.getStackInSlot(i) != null)
			{
				NBTTagCompound compoundTag = new NBTTagCompound();

				compoundTag.setInteger(NBT_SLOT, i);

				inventory.getStackInSlot(i).writeToNBT(compoundTag);

				tagList.appendTag(compoundTag);
			}
		}

		nbt.setTag(NBT_INVENTORY, tagList);
	}

	public static void restoreInventory(IInventory inventory, NBTTagCompound nbt)
	{
		if (nbt.hasKey(NBT_INVENTORY))
		{
			NBTTagList tagList = nbt.getTagList(NBT_INVENTORY, Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < tagList.tagCount(); i++)
			{
				int slot = tagList.getCompoundTagAt(i).getInteger(NBT_SLOT);

				ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	public static void dropInventory(World world, IInventory inventory, int x, int y, int z)
	{
		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null)
			{
				dropStack(world, stack, x, y, z);
			}
		}
	}

	public static void dropStack(World world, ItemStack stack, int x, int y, int z) // @TODO: Take BlockPos here
	{
		float xo = world.rand.nextFloat() * 0.8F + 0.1F;
		float yo = world.rand.nextFloat() * 0.8F + 0.1F;
		float zo = world.rand.nextFloat() * 0.8F + 0.1F;

		while (stack.stackSize > 0)
		{
			int amount = world.rand.nextInt(21) + 10;

			if (amount > stack.stackSize)
			{
				amount = stack.stackSize;
			}

			stack.stackSize -= amount;

			EntityItem entity = new EntityItem(world, (float) x + xo, (float) y + yo, (float) z + zo, new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

			entity.motionX = (float) world.rand.nextGaussian() * 0.05F;
			entity.motionY = (float) world.rand.nextGaussian() * 0.05F + 0.2F;
			entity.motionZ = (float) world.rand.nextGaussian() * 0.05F;

			if (stack.hasTagCompound())
			{
				entity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
			}

			world.spawnEntityInWorld(entity);
		}
	}

	public static void pushToInventorySlot(IInventory inventory, int i, ItemStack stack)
	{
		ItemStack slot = inventory.getStackInSlot(i);

		if (slot == null)
		{
			inventory.setInventorySlotContents(i, stack);
		}
		else if (compareStackNoQuantity(slot, stack))
		{
			slot.stackSize += stack.stackSize;
		}
	}

	public static boolean canPushToInventorySlot(IInventory inventory, int i, ItemStack stack)
	{
		ItemStack slot = inventory.getStackInSlot(i);

		if (slot == null)
		{
			return true;
		}

		if (!compareStackNoQuantity(slot, stack))
		{
			return false;
		}

		return slot.stackSize + stack.stackSize < slot.getMaxStackSize();
	}

	public static void pushToInventory(IInventory inventory, ItemStack stack)
	{
		int toGo = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot == null)
			{
				inventory.setInventorySlotContents(i, stack);

				return;
			}
			else if (compareStackNoQuantity(slot, stack))
			{
				int toAdd = toGo;

				if (slot.stackSize + toAdd > slot.getMaxStackSize())
				{
					toAdd = slot.getMaxStackSize() - slot.stackSize;
				}

				slot.stackSize += toAdd;

				toGo -= toAdd;

				if (toGo == 0)
				{
					return;
				}
			}
		}
	}

	public static boolean canPushToInventory(IInventory inventory, ItemStack stack)
	{
		int toGo = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot == null)
			{
				return true;
			}
			else if (compareStackNoQuantity(slot, stack))
			{
				int toAdd = toGo;

				if (slot.stackSize + toAdd > slot.getMaxStackSize())
				{
					toAdd = slot.getMaxStackSize() - slot.stackSize;
				}

				toGo -= toAdd;

				if (toGo == 0)
				{
					break;
				}
			}
		}

		return toGo == 0;
	}

	public static boolean compareStack(ItemStack first, ItemStack second)
	{
		return compareStack(first, second, COMPARE_NBT | COMPARE_DAMAGE | COMPARE_QUANTITY);
	}

	public static boolean compareStack(ItemStack first, ItemStack second, int flags)
	{
		if (first == null && second == null)
		{
			return true;
		}

		if ((first == null && second != null) || (first != null && second == null))
		{
			return false;
		}

		if ((flags & COMPARE_DAMAGE) == COMPARE_DAMAGE)
		{
			if (first.getItemDamage() != second.getItemDamage())
			{
				return false;
			}
		}

		if ((flags & COMPARE_NBT) == COMPARE_NBT)
		{
			if (first.hasTagCompound() && !first.getTagCompound().equals(second.getTagCompound()))
			{
				return false;
			}
		}

		if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY)
		{
			if (first.stackSize != second.stackSize)
			{
				return false;
			}
		}

		return first.getItem() == second.getItem();
	}

	public static boolean compareStackNoQuantity(ItemStack first, ItemStack second)
	{
		return compareStack(first, second, COMPARE_NBT | COMPARE_DAMAGE);
	}
}

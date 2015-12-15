package storagecraft.util;

import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class InventoryUtil {
	public static void saveInventory(IInventory inventory, NBTTagCompound nbt) {
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound compoundTag = new NBTTagCompound();

				compoundTag.setInteger("Slot", i);

				inventory.getStackInSlot(i).writeToNBT(compoundTag);

				tagList.appendTag(compoundTag);
			}
		}

		nbt.setTag("Inventory", tagList);
	}

	public static void restoreInventory(IInventory inventory, NBTTagCompound nbt) {
		if (nbt.hasKey("Inventory")) {
			NBTTagList tagList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < tagList.tagCount(); i++) {
				int slot = tagList.getCompoundTagAt(i).getInteger("Slot");

				ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	// https://github.com/cpw/ironchest/blob/master/src/main/java/cpw/mods/ironchest/BlockIronChest.java#L200
	public static void dropInventory(World world, IInventory inventory, int x, int y, int z, int newSize) {
		Random random = world.rand;

		for (int i = newSize; i < inventory.getSizeInventory(); ++i) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack == null) {
				continue;
			}

			float xo = random.nextFloat() * 0.8F + 0.1F;
			float yo = random.nextFloat() * 0.8F + 0.1F;
			float zo = random.nextFloat() * 0.8F + 0.1F;

			while (stack.stackSize > 0) {
				int amount = random.nextInt(21) + 10;

				if (amount > stack.stackSize) {
					amount = stack.stackSize;
				}

				stack.stackSize -= amount;

				EntityItem entity = new EntityItem(world, (float) x + xo, (float) y + (newSize > 0 ? 1 : 0) + yo, (float) z + zo, new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

				entity.motionX = (float) random.nextGaussian() * 0.05F;
				entity.motionY = (float) random.nextGaussian() * 0.05F + 0.2F;
				entity.motionZ = (float) random.nextGaussian() * 0.05F;

				if (stack.hasTagCompound()) {
					entity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
				}

				world.spawnEntityInWorld(entity);
			}
		}
	}

	public static boolean equalsIgnoreQuantity(ItemStack first, ItemStack second) {
		if (first.stackTagCompound != null && !first.stackTagCompound.equals(second.stackTagCompound)) {
			return false;
		}

		return first.getItem() == second.getItem() && first.getItemDamage() == second.getItemDamage();
	}
}

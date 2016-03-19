package storagecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemPattern extends ItemBase
{
	public static final String NBT_SLOT = "Slot_%d";

	public ItemPattern()
	{
		super("pattern");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if (isValid(player.worldObj, stack))
		{
			ItemStack result = getPatternResult(player.worldObj, stack);

			list.add(I18n.translateToLocalFormatted("misc.storagecraft:pattern.tooltip", result.stackSize, result.getDisplayName()));
		}
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		if (hasPattern(stack))
		{
			return 1;
		}

		return 64;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (hasPattern(stack))
		{
			return "item.storagecraft:pattern";
		}

		return "item.storagecraft:pattern.blank";
	}

	public static boolean isValid(World world, ItemStack stack)
	{
		return stack.getTagCompound() != null && hasPattern(stack) && getPatternResult(world, stack) != null;
	}

	public static ItemStack[] getPattern(ItemStack stack)
	{
		ItemStack[] pattern = new ItemStack[9];

		if (stack.getTagCompound() != null)
		{
			for (int i = 0; i < 9; ++i)
			{
				String name = String.format(NBT_SLOT, i);

				if (stack.getTagCompound().hasKey(name))
				{
					pattern[i] = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(name));
				}
			}
		}

		return pattern;
	}

	public static boolean hasPattern(ItemStack stack)
	{
		int empty = 0;

		for (ItemStack slot : getPattern(stack))
		{
			if (slot == null)
			{
				empty++;
			}
		}

		return empty != 9;
	}

	public static ItemStack getPatternResult(World world, ItemStack stack)
	{
		InventoryCrafting crafting = new InventoryCrafting(new Container()
		{
			@Override
			public boolean canInteractWith(EntityPlayer player)
			{
				return false;
			}
		}, 3, 3);

		ItemStack[] pattern = getPattern(stack);

		for (int i = 0; i < 9; ++i)
		{
			crafting.setInventorySlotContents(i, pattern[i]);
		}

		return CraftingManager.getInstance().findMatchingRecipe(crafting, world);
	}

	public static ItemStack setPattern(ItemStack stack, ItemStack slot, int id)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound slotTag = new NBTTagCompound();

		slot.writeToNBT(slotTag);

		stack.getTagCompound().setTag(String.format(NBT_SLOT, id), slotTag);

		return stack;
	}
}

package refinedstorage.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;

public abstract class ItemBase extends Item
{
	private String name;

	public ItemBase(String name)
	{
		this.name = name;

		setCreativeTab(RefinedStorage.TAB);
	}

	@Override
	public String getUnlocalizedName()
	{
		return "item." + RefinedStorage.ID + ":" + name;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (getHasSubtypes())
		{
			return getUnlocalizedName() + "." + stack.getItemDamage();
		}

		return getUnlocalizedName();
	}
}

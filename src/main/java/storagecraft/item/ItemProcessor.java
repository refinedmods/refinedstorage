package storagecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemProcessor extends ItemBase
{
	public static final int TYPE_PRINTED_BASIC = 0;
	public static final int TYPE_PRINTED_IMPROVED = 1;
	public static final int TYPE_PRINTED_ADVANCED = 2;
	public static final int TYPE_BASIC = 3;
	public static final int TYPE_IMPROVED = 4;
	public static final int TYPE_ADVANCED = 5;
	public static final int TYPE_PRINTED_SILICON = 6;

	public ItemProcessor()
	{
		super("processor");

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i <= 6; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}

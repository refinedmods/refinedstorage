package storagecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStoragePart extends ItemBase
{
	public static final int TYPE_1K = 0;
	public static final int TYPE_4K = 1;
	public static final int TYPE_16K = 2;
	public static final int TYPE_64K = 3;

	public ItemStoragePart()
	{
		super("storage_part");

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i <= 3; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}

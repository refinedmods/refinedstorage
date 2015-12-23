package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemCore extends ItemBase
{
	public static final int TYPE_CONSTRUCTION = 0;
	public static final int TYPE_DESTRUCTION = 1;

	private IIcon constructionIcon;
	private IIcon destructionIcon;

	public ItemCore()
	{
		super("core");

		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < 2; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		constructionIcon = register.registerIcon("storagecraft:core0");
		destructionIcon = register.registerIcon("storagecraft:core1");
	}

	@Override
	public IIcon getIconFromDamage(int damage)
	{
		switch (damage)
		{
			case TYPE_CONSTRUCTION:
				return constructionIcon;
			case TYPE_DESTRUCTION:
				return destructionIcon;
			default:
				return null;
		}
	}
}

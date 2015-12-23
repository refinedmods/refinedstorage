package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemProcessor extends ItemBase
{
	public static final int TYPE_PRINTED_BASIC = 0;
	public static final int TYPE_PRINTED_IMPROVED = 1;
	public static final int TYPE_PRINTED_ADVANCED = 2;
	public static final int TYPE_BASIC = 3;
	public static final int TYPE_IMPROVED = 4;
	public static final int TYPE_ADVANCED = 5;
	public static final int TYPE_PRINTED_SILICON = 6;

	private IIcon[] icons = new IIcon[7];

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

	@Override
	public void registerIcons(IIconRegister register)
	{
		for (int i = 0; i <= 6; ++i)
		{
			icons[i] = register.registerIcon("storagecraft:processor" + i);
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}

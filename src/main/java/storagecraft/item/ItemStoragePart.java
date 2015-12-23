package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemStoragePart extends ItemBase
{
	public static final int TYPE_1K = 0;
	public static final int TYPE_4K = 1;
	public static final int TYPE_16K = 2;
	public static final int TYPE_64K = 3;

	private IIcon[] icons = new IIcon[4];

	public ItemStoragePart()
	{
		super("storagePart");

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

	@Override
	public void registerIcons(IIconRegister register)
	{
		for (int i = 0; i <= 3; ++i)
		{
			icons[i] = register.registerIcon("storagecraft:storagePart" + i);
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage)
	{
		return icons[damage];
	}
}

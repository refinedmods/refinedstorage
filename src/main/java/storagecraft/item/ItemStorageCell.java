package storagecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.storage.CellStorage;

import java.util.List;

public class ItemStorageCell extends ItemBase
{
	public static final int TYPE_1K = 0;
	public static final int TYPE_4K = 1;
	public static final int TYPE_16K = 2;
	public static final int TYPE_64K = 3;
	public static final int TYPE_CREATIVE = 4;

	public ItemStorageCell()
	{
		super("storage_cell");

		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < 5; ++i)
		{
			list.add(initNBT(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public void addInformation(ItemStack cell, EntityPlayer player, List list, boolean b)
	{
		if (getCapacity(cell) == -1)
		{
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storage_cell_stored"), getStored(cell)));
		}
		else
		{
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storage_cell_stored_capacity"), getStored(cell), getCapacity(cell)));
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
		super.onCreated(stack, world, player);

		initNBT(stack);
	}

	private ItemStack initNBT(ItemStack cell)
	{
		cell.setTagCompound(new NBTTagCompound());

		cell.getTagCompound().setTag(CellStorage.NBT_ITEMS, new NBTTagList());
		cell.getTagCompound().setInteger(CellStorage.NBT_STORED, 0);

		return cell;
	}

	public static int getStored(ItemStack cell)
	{
		return cell.getTagCompound().getInteger(CellStorage.NBT_STORED);
	}

	public static int getCapacity(ItemStack cell)
	{
		switch (cell.getItemDamage())
		{
			case TYPE_1K:
				return 1000;
			case TYPE_4K:
				return 4000;
			case TYPE_16K:
				return 16000;
			case TYPE_64K:
				return 64000;
			case TYPE_CREATIVE:
				return -1;
		}

		return 0;
	}
}

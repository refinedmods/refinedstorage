package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import storagecraft.storage.CellStorage;

public class ItemStorageCell extends ItemSC {
	private IIcon[] icons = new IIcon[5];

	public ItemStorageCell() {
		super("storageCell");

		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 5; ++i) {
			list.add(initNBT(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public void addInformation(ItemStack cell, EntityPlayer player, List list, boolean b) {
		if (getCapacity(cell) == -1) {
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storageCellStored"), getStored(cell)));
		} else {
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storageCellStoredWithCapacity"), getStored(cell), getCapacity(cell)));
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);

		initNBT(stack);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		for (int i = 0; i < 5; ++i) {
			icons[i] = register.registerIcon("storagecraft:storageCell" + i);
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icons[damage];
	}

	private ItemStack initNBT(ItemStack cell) {
		cell.stackTagCompound = new NBTTagCompound();
		cell.stackTagCompound.setTag(CellStorage.NBT_ITEMS, new NBTTagList());
		cell.stackTagCompound.setInteger(CellStorage.NBT_STORED, 0);

		return cell;
	}

	public static int getStored(ItemStack cell) {
		return cell.stackTagCompound.getInteger(CellStorage.NBT_STORED);
	}

	public static int getCapacity(ItemStack cell) {
		switch (cell.getItemDamage()) {
			case 0:
				return 1000;
			case 1:
				return 4000;
			case 2:
				return 16000;
			case 3:
				return 64000;
			case 4:
				return -1;
		}

		return 0;
	}
}

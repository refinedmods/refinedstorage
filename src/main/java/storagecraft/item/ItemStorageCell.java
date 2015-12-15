package storagecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
			list.add(CellStorage.init(new ItemStack(item, 1, i)));
		}
	}

	@Override
	public void addInformation(ItemStack cell, EntityPlayer player, List list, boolean b) {
		if (CellStorage.getCapacity(cell) == -1) {
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storageCellStored"), CellStorage.getStored(cell)));
		} else {
			list.add(String.format(StatCollector.translateToLocal("misc.storagecraft:storageCellStoredWithCapacity"), CellStorage.getStored(cell), CellStorage.getCapacity(cell)));
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);

		CellStorage.init(stack);
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
}

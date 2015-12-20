package storagecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraft;

public abstract class ItemBase extends Item {
	private String name;

	public ItemBase(String name) {
		this.name = name;

		setCreativeTab(StorageCraft.TAB);
		setTextureName("storagecraft:" + name);
	}

	@Override
	public String getUnlocalizedName() {
		return "item." + StorageCraft.ID + ":" + name;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (getHasSubtypes()) {
			return getUnlocalizedName() + "." + stack.getItemDamage();
		}

		return getUnlocalizedName();
	}
}

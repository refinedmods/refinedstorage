package storagecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.SC;

public class ItemSC extends Item {
	private String name;

	public ItemSC(String name) {
		this.name = name;

		setCreativeTab(SC.TAB);
		setTextureName("storagecraft:" + name);
	}

	@Override
	public String getUnlocalizedName() {
		return "item." + SC.ID + ":" + name;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName();
	}
}

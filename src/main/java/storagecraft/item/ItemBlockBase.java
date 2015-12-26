package storagecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockBase extends ItemColored
{
	public ItemBlockBase(Block block)
	{
		super(block, true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return getUnlocalizedName() + "." + stack.getItemDamage();
	}
}

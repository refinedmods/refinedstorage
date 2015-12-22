package storagecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockBase extends ItemBlockWithMetadata
{
	public ItemBlockBase(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return getUnlocalizedName() + "." + stack.getItemDamage();
	}
}

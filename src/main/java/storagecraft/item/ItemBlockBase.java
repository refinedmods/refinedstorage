package storagecraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class ItemBlockBase extends ItemBlock
{
	public ItemBlockBase(Block block)
	{
		super(block);

		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return getUnlocalizedName() + "." + stack.getItemDamage();
	}
}

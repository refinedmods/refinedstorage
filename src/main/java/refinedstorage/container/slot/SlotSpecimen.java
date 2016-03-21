package refinedstorage.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSpecimen extends Slot
{
	private boolean sizeAllowed;

	public SlotSpecimen(IInventory inventory, int id, int x, int y, boolean allowSize)
	{
		super(inventory, id, x, y);

		this.sizeAllowed = allowSize;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return true;
	}

	@Override
	public void putStack(ItemStack stack)
	{
		if (stack != null && !sizeAllowed)
		{
			stack.stackSize = 1;
		}

		super.putStack(stack);
	}

	public boolean isSizeAllowed()
	{
		return sizeAllowed;
	}
}

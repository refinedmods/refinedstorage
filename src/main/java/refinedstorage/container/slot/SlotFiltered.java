package refinedstorage.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFiltered extends Slot
{
	private IItemValidator validator;

	public SlotFiltered(IInventory inventory, int id, int x, int y, IItemValidator validator)
	{
		super(inventory, id, x, y);

		this.validator = validator;
	}

	@Override
	public boolean isItemValid(ItemStack item)
	{
		return validator.isValid(item);
	}
}

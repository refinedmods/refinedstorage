package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.BasicItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.tile.TileDrive;

public class ContainerDrive extends ContainerBase
{
	public ContainerDrive(EntityPlayer player, TileDrive drive)
	{
		super(player);

		int x = 8;
		int y = 20;

		for (int i = 0; i < 8; ++i)
		{
			addSlotToContainer(new SlotFiltered(drive, i, x, y, new BasicItemValidator(RefinedStorageItems.STORAGE_CELL)));

			if ((i + 1) % 2 == 0)
			{
				x = 8;
				y += 18;
			}
			else
			{
				x += 18;
			}
		}

		addPlayerInventory(8, 108);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stack = null;

		Slot slot = getSlot(index);

		if (slot != null && slot.getHasStack())
		{
			stack = slot.getStack().copy();

			if (index < 8)
			{
				if (!mergeItemStack(stack, 8, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!mergeItemStack(stack, 0, 8, false))
			{
				return null;
			}

			if (stack.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}
}

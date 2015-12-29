package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftItems;
import storagecraft.container.slot.SlotItemFilter;
import storagecraft.container.slot.SlotOutput;
import storagecraft.tile.TileWirelessTransmitter;

public class ContainerWirelessTransmitter extends ContainerBase
{
	public ContainerWirelessTransmitter(EntityPlayer player, TileWirelessTransmitter wirelessTransmitter)
	{
		super(player);

		addSlotToContainer(new SlotItemFilter(wirelessTransmitter, 0, 8, 20, Items.ender_pearl));
		addSlotToContainer(new SlotItemFilter(wirelessTransmitter, 1, 101, 20, StorageCraftItems.WIRELESS_GRID));
		addSlotToContainer(new SlotOutput(wirelessTransmitter, 2, 152, 20));

		addPlayerInventory(8, 55);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stack = null;

		Slot slot = getSlot(index);

		if (slot != null && slot.getHasStack())
		{
			stack = slot.getStack().copy();

			if (index < 3)
			{
				if (!mergeItemStack(stack, 3, inventorySlots.size(), true))
				{
					return null;
				}
			} else if (!mergeItemStack(stack, 0, 3, false))
			{
				return null;
			}

			if (stack.stackSize == 0)
			{
				slot.putStack(null);
			} else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}
}

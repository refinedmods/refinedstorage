package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import storagecraft.StorageCraftItems;
import storagecraft.container.slot.SlotItemFilter;
import storagecraft.container.slot.SlotOutput;
import storagecraft.tile.TileWirelessTransmitter;

public class ContainerWirelessTransmitter extends ContainerBase
{
	public ContainerWirelessTransmitter(EntityPlayer player, TileWirelessTransmitter wirelessTransmitter)
	{
		super(player);

		addPlayerInventory(8, 55);

		addSlotToContainer(new SlotItemFilter(wirelessTransmitter, 0, 8, 20, Items.ender_pearl));
		addSlotToContainer(new SlotItemFilter(wirelessTransmitter, 1, 101, 20, StorageCraftItems.WIRELESS_GRID));
		addSlotToContainer(new SlotOutput(wirelessTransmitter, 2, 152, 20));
	}
}

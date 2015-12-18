package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import storagecraft.container.slot.SlotSpecimen;

public class ContainerBase extends Container {
	private EntityPlayer player;

	public ContainerBase(EntityPlayer player) {
		this.player = player;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	protected void addPlayerInventory(int xInventory, int yInventory) {
		int id = 0;

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, id, xInventory + i * 18, yInventory + 4 + (3 * 18)));

			id++;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));

				id++;
			}
		}
	}

	@Override
	public ItemStack slotClick(int id, int clickedButton, int mode, EntityPlayer player) {
		Slot slot = id >= 0 ? getSlot(id) : null;

		if (slot instanceof SlotSpecimen) {
			if (clickedButton == 2) {
				slot.putStack(null);
			} else {
				slot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
			}

			return player.inventory.getItemStack();
		}

		return super.slotClick(id, clickedButton, mode, player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}

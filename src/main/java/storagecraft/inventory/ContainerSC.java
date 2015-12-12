package storagecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSC extends Container {
	private EntityPlayer player;

	public ContainerSC(EntityPlayer player) {
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
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}

package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileImporter extends TileMachine implements IInventory {
	private InventorySimple inventory = new InventorySimple("importer", 9);

	private int compareFlags = InventoryUtils.COMPARE_NBT | InventoryUtils.COMPARE_DAMAGE;

	private int currentSlot = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote && isConnected()) {
			TileEntity tile = worldObj.getTileEntity(xCoord + getDirection().offsetX, yCoord + getDirection().offsetY, zCoord + getDirection().offsetZ);

			if (tile instanceof IInventory) {
				IInventory connectedInventory = (IInventory) tile;

				if (ticks % 5 == 0) {
					ItemStack slot;

					while ((slot = connectedInventory.getStackInSlot(currentSlot)) == null) {
						currentSlot++;

						if (currentSlot > connectedInventory.getSizeInventory() - 1) {
							break;
						}
					}

					if (slot != null && canImport(slot)) {
						if (getController().push(slot.copy())) {
							connectedInventory.setInventorySlotContents(currentSlot, null);
							connectedInventory.markDirty();
						}
					}

					currentSlot++;

					if (currentSlot > connectedInventory.getSizeInventory() - 1) {
						currentSlot = 0;
					}
				}
			}
		}
	}

	public boolean canImport(ItemStack stack) {
		int slots = 0;

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot != null) {
				slots++;

				if (InventoryUtils.compareStack(stack, slot, compareFlags)) {
					return true;
				}
			}
		}

		return slots == 0;
	}

	public int getCompareFlags() {
		return compareFlags;
	}

	public void setCompareFlags(int flags) {
		this.compareFlags = flags;
	}

	@Override
	public int getEnergyUsage() {
		return 3;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {
		inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		inventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("CompareFlags")) {
			compareFlags = nbt.getInteger("CompareFlags");
		}

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("CompareFlags", compareFlags);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		compareFlags = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(compareFlags);
	}
}

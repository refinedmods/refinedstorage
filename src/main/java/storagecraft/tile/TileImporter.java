package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileImporter extends TileMachine implements IInventory, ISidedInventory {
	public static final String NBT_COMPARE_FLAGS = "CompareFlags";
	public static final String NBT_MODE = "Mode";

	private InventorySimple inventory = new InventorySimple("importer", 9);

	private int compareFlags = 0;
	private ImporterMode mode = ImporterMode.WHITELIST;

	private int currentSlot = 0;

	@Override
	public int getEnergyUsage() {
		return 2;
	}

	@Override
	public void updateMachine() {
		TileEntity tile = worldObj.getTileEntity(xCoord + getDirection().offsetX, yCoord + getDirection().offsetY, zCoord + getDirection().offsetZ);

		if (tile instanceof IInventory) {
			IInventory connectedInventory = (IInventory) tile;

			if (ticks % 5 == 0) {
				ItemStack slot = connectedInventory.getStackInSlot(currentSlot);

				while ((slot = connectedInventory.getStackInSlot(currentSlot)) == null) {
					currentSlot++;

					if (currentSlot > connectedInventory.getSizeInventory() - 1) {
						break;
					}
				}

				if (slot != null && canImport(slot)) {
					if (connectedInventory instanceof ISidedInventory) {
						ISidedInventory sided = (ISidedInventory) connectedInventory;

						if (sided.canExtractItem(currentSlot, slot.copy(), getDirection().getOpposite().ordinal())) {
							if (getController().push(slot.copy())) {
								connectedInventory.setInventorySlotContents(currentSlot, null);
							}
						}
					} else if (getController().push(slot.copy())) {
						connectedInventory.setInventorySlotContents(currentSlot, null);
					}

					connectedInventory.markDirty();
				}

				currentSlot++;

				if (currentSlot > connectedInventory.getSizeInventory() - 1) {
					currentSlot = 0;
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
					if (mode == ImporterMode.WHITELIST) {
						return true;
					} else if (mode == ImporterMode.BLACKLIST) {
						return false;
					}
				}
			}
		}

		if (mode == ImporterMode.WHITELIST) {
			return slots == 0;
		}

		return true;
	}

	public int getCompareFlags() {
		return compareFlags;
	}

	public void setCompareFlags(int flags) {
		this.compareFlags = flags;
	}

	public ImporterMode getMode() {
		return mode;
	}

	public void setMode(ImporterMode mode) {
		this.mode = mode;
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
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey(NBT_COMPARE_FLAGS)) {
			compareFlags = nbt.getInteger(NBT_COMPARE_FLAGS);
		}

		if (nbt.hasKey(NBT_MODE)) {
			mode = ImporterMode.getById(nbt.getInteger(NBT_MODE));
		}

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE_FLAGS, compareFlags);
		nbt.setInteger(NBT_MODE, mode.id);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		compareFlags = buf.readInt();
		mode = ImporterMode.getById(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(compareFlags);
		buf.writeInt(mode.id);
	}
}

package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.inventory.InventorySimple;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileDetector extends TileMachine implements IInventory, ISidedInventory, ICompareSetting {
	public static final int MODE_UNDER = 0;
	public static final int MODE_EQUAL = 1;
	public static final int MODE_ABOVE = 2;

	public static final String NBT_COMPARE = "Compare";
	public static final String NBT_MODE = "Mode";
	public static final String NBT_AMOUNT = "Amount";

	private InventorySimple inventory = new InventorySimple("detector", 1);

	private int compare = 0;
	private int mode = MODE_EQUAL;
	private int amount = 0;

	private boolean providesPower = false;

	@Override
	public int getEnergyUsage() {
		return 4;
	}

	@Override
	public void updateMachine() {
		if (ticks % 5 == 0) {
			ItemStack slot = inventory.getStackInSlot(0);

			if (slot != null) {
				boolean foundAny = false;

				for (StorageItem item : getController().getItems()) {
					if (item.compare(slot, compare)) {
						foundAny = true;

						switch (mode) {
							case MODE_UNDER:
								providesPower = item.getQuantity() < amount;
								break;
							case MODE_EQUAL:
								providesPower = item.getQuantity() == amount;
								break;
							case MODE_ABOVE:
								providesPower = item.getQuantity() > amount;
								break;
						}
					}
				}

				if (!foundAny) {
					switch (mode) {
						case MODE_UNDER:
							providesPower = amount != 0;
							break;
						case MODE_EQUAL:
							providesPower = amount == 0;
							break;
						default:
							providesPower = false;
							break;
					}
				}
			} else {
				providesPower = false;
			}
		}
	}

	public boolean providesPower() {
		return providesPower;
	}

	@Override
	public int getCompare() {
		return compare;
	}

	@Override
	public void setCompare(int compare) {
		this.compare = compare;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

		if (nbt.hasKey(NBT_COMPARE)) {
			compare = nbt.getInteger(NBT_COMPARE);
		}

		if (nbt.hasKey(NBT_MODE)) {
			mode = nbt.getInteger(NBT_MODE);
		}

		if (nbt.hasKey(NBT_AMOUNT)) {
			amount = nbt.getInteger(NBT_AMOUNT);
		}

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE, compare);
		nbt.setInteger(NBT_MODE, mode);
		nbt.setInteger(NBT_AMOUNT, amount);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);

		compare = buf.readInt();
		mode = buf.readInt();
		amount = buf.readInt();
		providesPower = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);

		buf.writeInt(compare);
		buf.writeInt(mode);
		buf.writeInt(amount);
		buf.writeBoolean(providesPower);
	}
}

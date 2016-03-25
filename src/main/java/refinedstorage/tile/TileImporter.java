package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.settings.ICompareSetting;
import refinedstorage.tile.settings.IModeSetting;
import refinedstorage.util.InventoryUtils;

public class TileImporter extends TileMachine implements ICompareSetting, IModeSetting {
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("importer", 9, this);

    private int compare = 0;
    private int mode = 0;

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
        if (ticks % 5 == 0) {
            TileEntity connectedTile = worldObj.getTileEntity(pos.offset(getDirection()));

            if (connectedTile instanceof ISidedInventory) {
                ISidedInventory sided = (ISidedInventory) connectedTile;

                int[] availableSlots = sided.getSlotsForFace(getDirection().getOpposite());

                for (int availableSlot : availableSlots) {
                    ItemStack stack = sided.getStackInSlot(availableSlot);

                    if (stack != null && canImport(stack) && sided.canExtractItem(availableSlot, stack, getDirection().getOpposite())) {
                        if (getController().push(stack.copy())) {
                            sided.setInventorySlotContents(availableSlot, null);
                            sided.markDirty();
                        }
                    }
                }
            } else if (connectedTile instanceof IInventory) {
                IInventory inventory = (IInventory) connectedTile;

                for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                    ItemStack stack = inventory.getStackInSlot(i);

                    if (stack != null && canImport(stack)) {
                        if (getController().push(stack.copy())) {
                            inventory.setInventorySlotContents(i, null);
                            inventory.markDirty();
                        }
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

                if (InventoryUtils.compareStack(stack, slot, compare)) {
                    if (isWhitelist()) {
                        return true;
                    } else if (isBlacklist()) {
                        return false;
                    }
                }
            }
        }

        if (isWhitelist()) {
            return slots == 0;
        }

        return true;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        markDirty();

        this.compare = compare;
    }

    @Override
    public boolean isWhitelist() {
        return mode == 0;
    }

    @Override
    public boolean isBlacklist() {
        return mode == 1;
    }

    @Override
    public void setToWhitelist() {
        markDirty();

        this.mode = 0;
    }

    @Override
    public void setToBlacklist() {
        markDirty();

        this.mode = 1;
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

        InventoryUtils.restoreInventory(inventory, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);

        InventoryUtils.saveInventory(inventory, 0, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    public IInventory getInventory() {
        return inventory;
    }
}

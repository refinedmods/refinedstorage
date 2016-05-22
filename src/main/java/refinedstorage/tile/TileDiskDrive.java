package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.block.EnumStorageType;
import refinedstorage.container.ContainerDiskDrive;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.storage.*;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConstants;

import java.util.List;

public class TileDiskDrive extends TileMachine implements IStorageProvider, IStorageGui, ICompareConfig, IModeConfig, IInventory {
    public static final String NBT_PRIORITY = "Priority";
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("disk_drive", 8, this);
    private InventorySimple filterInventory = new InventorySimple("filters", 9, this);

    private NBTStorage storages[] = new NBTStorage[8];

    private int priority = 0;
    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    public NBTStorage getStorage(int slot) {
        if (inventory.getStackInSlot(slot) == null) {
            storages[slot] = null;
        } else if (storages[slot] == null) {
            storages[slot] = new DiskStorage(getStackInSlot(slot), this);
        }

        return storages[slot];
    }

    @Override
    public int getEnergyUsage() {
        int base = 5;

        for (int i = 0; i < getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null) {
                base += 2;
            }
        }

        return base;
    }

    @Override
    public void updateMachine() {
        for (int i = 0; i < getSizeInventory(); ++i) {
            NBTStorage storage = getStorage(i);

            if (storage != null && storage.isDirty()) {
                storage.writeToNBT(getStackInSlot(i).getTagCompound());
                storage.markClean();
            }
        }
    }

    @Override
    public void provide(List<IStorage> storages) {
        for (int i = 0; i < getSizeInventory(); ++i) {
            NBTStorage storage = getStorage(i);

            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(filterInventory, 1, nbt);

        if (nbt.hasKey(NBT_PRIORITY)) {
            priority = nbt.getInteger(NBT_PRIORITY);
        }

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
        RefinedStorageUtils.saveInventory(filterInventory, 1, nbt);

        nbt.setInteger(NBT_PRIORITY, priority);
        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(priority);
        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        priority = buf.readInt();
        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerDiskDrive.class;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:disk_drive.name";
    }

    @Override
    public IInventory getInventory() {
        return filterInventory;
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeConfig() {
        return this;
    }

    @Override
    public ICompareConfig getCompareConfig() {
        return this;
    }

    @Override
    public IModeConfig getModeConfig() {
        return this;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    @Override
    public int getStored() {
        int stored = 0;

        for (int i = 0; i < getSizeInventory(); ++i) {
            ItemStack stack = getStackInSlot(i);

            if (stack != null) {
                stored += NBTStorage.getStored(stack.getTagCompound());
            }
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (int i = 0; i < getSizeInventory(); ++i) {
            ItemStack stack = getStackInSlot(i);

            if (stack != null) {
                int diskCapacity = EnumStorageType.getById(stack.getItemDamage()).getCapacity();

                if (diskCapacity == -1) {
                    return -1;
                }

                capacity += diskCapacity;
            }
        }

        return capacity;
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
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
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
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return stack.getItem() == RefinedStorageItems.STORAGE_DISK;
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public IInventory getDroppedInventory() {
        return inventory;
    }
}

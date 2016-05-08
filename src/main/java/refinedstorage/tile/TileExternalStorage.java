package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.storage.IStorage;
import refinedstorage.storage.IStorageGui;
import refinedstorage.storage.IStorageProvider;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConfigUtils;

import java.util.List;

public class TileExternalStorage extends TileMachine implements IStorageProvider, IStorage, IStorageGui, ICompareConfig, IModeConfig {
    public static final String NBT_PRIORITY = "Priority";
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("external_storage", 9, this);

    private int priority = 0;
    private int compare = 0;
    private int mode = 0;

    private int stored = 0;

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void addItems(List<ItemGroup> items) {
        TileEntity connectedTile = getConnectedTile();

        if (connectedTile instanceof IDeepStorageUnit) {
            IDeepStorageUnit deep = (IDeepStorageUnit) connectedTile;

            if (deep.getStoredItemType() != null) {
                ItemStack stack = deep.getStoredItemType().copy();

                while (stack.stackSize > 0) {
                    items.add(new ItemGroup(stack.splitStack(Math.min(stack.getMaxStackSize(), stack.stackSize))));
                }
            }
        } else if (connectedTile instanceof IInventory) {
            IInventory inventory = (IInventory) connectedTile;

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                if (inventory.getStackInSlot(i) != null) {
                    items.add(new ItemGroup(inventory.getStackInSlot(i)));
                }
            }
        }
    }

    @Override
    public void push(ItemStack stack) {
        TileEntity connectedTile = getConnectedTile();

        if (connectedTile instanceof IDeepStorageUnit) {
            IDeepStorageUnit deep = (IDeepStorageUnit) connectedTile;

            if (deep.getStoredItemType() == null) {
                deep.setStoredItemType(stack, stack.stackSize);
            } else {
                deep.setStoredItemCount(deep.getStoredItemType().stackSize + stack.stackSize);
            }
        } else if (connectedTile instanceof IInventory) {
            RefinedStorageUtils.pushToInventory((IInventory) connectedTile, stack);
        }
    }

    @Override
    public ItemStack take(ItemStack stack, int flags) {
        TileEntity connectedTile = getConnectedTile();

        int quantity = stack.stackSize;

        if (connectedTile instanceof IDeepStorageUnit) {
            IDeepStorageUnit deep = (IDeepStorageUnit) connectedTile;

            if (deep.getStoredItemType() != null && RefinedStorageUtils.compareStackNoQuantity(deep.getStoredItemType(), stack)) {
                if (deep.getStoredItemType().stackSize < quantity) {
                    quantity = deep.getStoredItemType().stackSize;
                }

                ItemStack took = deep.getStoredItemType().copy();
                took.stackSize = quantity;

                deep.setStoredItemCount(deep.getStoredItemType().stackSize - quantity);

                return took;
            }
        } else if (connectedTile instanceof IInventory) {
            IInventory inventory = (IInventory) connectedTile;

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack slot = inventory.getStackInSlot(i);

                if (slot != null && RefinedStorageUtils.compareStack(slot, stack, flags)) {
                    if (quantity > slot.stackSize) {
                        quantity = slot.stackSize;
                    }

                    slot.stackSize -= quantity;

                    if (slot.stackSize == 0) {
                        inventory.setInventorySlotContents(i, null);
                    }

                    ItemStack newItem = slot.copy();

                    newItem.stackSize = quantity;

                    return newItem;
                }
            }
        }

        return null;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        if (ModeConfigUtils.doesNotViolateMode(inventory, this, compare, stack)) {
            TileEntity connectedTile = getConnectedTile();

            if (connectedTile instanceof IDeepStorageUnit) {
                IDeepStorageUnit deep = (IDeepStorageUnit) connectedTile;

                if (deep.getStoredItemType() != null) {
                    if (RefinedStorageUtils.compareStackNoQuantity(deep.getStoredItemType(), stack)) {
                        return (deep.getStoredItemType().stackSize + stack.stackSize) < deep.getMaxStoredCount();
                    }

                    return false;
                } else {
                    return stack.stackSize < deep.getMaxStoredCount();
                }
            } else if (connectedTile instanceof IInventory) {
                return RefinedStorageUtils.canPushToInventory((IInventory) connectedTile, stack);
            }
        }

        return false;
    }

    public TileEntity getConnectedTile() {
        if (worldObj == null) {
            return null;
        }

        TileEntity tile = worldObj.getTileEntity(pos.offset(getDirection()));

        if (tile instanceof IInventory || tile instanceof IDeepStorageUnit) {
            return tile;
        }

        return null;
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(priority);

        TileEntity connectedTile = getConnectedTile();

        if (connectedTile instanceof IDeepStorageUnit) {
            IDeepStorageUnit deep = (IDeepStorageUnit) connectedTile;

            buf.writeInt(deep.getStoredItemType() == null ? 0 : deep.getStoredItemType().stackSize);
        } else if (connectedTile instanceof IInventory) {
            buf.writeInt(RefinedStorageUtils.getInventoryItemCount((IInventory) connectedTile));
        } else {
            buf.writeInt(0);
        }

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        priority = buf.readInt();
        stored = buf.readInt();
        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerStorage.class;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);

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

        nbt.setInteger(NBT_PRIORITY, priority);
        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);
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
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        markDirty();

        this.priority = priority;
    }

    @Override
    public void provide(List<IStorage> storages) {
        storages.add(this);
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:external_storage";
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
    public int getStored() {
        return stored;
    }

    @Override
    public int getCapacity() {
        if (getConnectedTile() == null) {
            return 0;
        }

        TileEntity connectedInventory = getConnectedTile();

        if (connectedInventory instanceof IDeepStorageUnit) {
            return ((IDeepStorageUnit) connectedInventory).getMaxStoredCount();
        } else if (connectedInventory instanceof IInventory) {
            return ((IInventory) connectedInventory).getSizeInventory() * 64;
        }

        return 0;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }
}

package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageCapabilities;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConstants;

import java.util.List;

public class TileExternalStorage extends TileMachine implements IStorageProvider, IStorage, IStorageGui, ICompareConfig, IModeConfig {
    public static final String NBT_PRIORITY = "Priority";
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private BasicItemHandler filters = new BasicItemHandler(9, this);

    private int priority = 0;
    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    private int stored = 0;

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void addItems(List<ItemStack> items) {
        IDeepStorageUnit storageUnit = getStorageUnit();

        if (storageUnit != null) {
            if (storageUnit.getStoredItemType() != null && storageUnit.getStoredItemType().getItem() != null) {
                items.add(storageUnit.getStoredItemType().copy());
            }
        } else {
            IItemHandler handler = getItemHandler();

            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getItem() != null) {
                        items.add(handler.getStackInSlot(i).copy());
                    }
                }
            }
        }
    }

    @Override
    public ItemStack push(ItemStack stack, boolean simulate) {
        IDeepStorageUnit storageUnit = getStorageUnit();

        // @todo: fix push for deep storage units
        if (storageUnit != null) {
            if (storageUnit.getStoredItemType() == null) {
                storageUnit.setStoredItemType(stack.copy(), stack.stackSize);
            } else {
                storageUnit.setStoredItemCount(storageUnit.getStoredItemType().stackSize + stack.stackSize);
            }
        } else {
            IItemHandler handler = getItemHandler();

            if (handler != null) {
                return ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
            }
        }

        return stack;
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        IDeepStorageUnit storageUnit = getStorageUnit();

        if (storageUnit != null) {
            if (storageUnit.getStoredItemType() != null && RefinedStorageUtils.compareStackNoQuantity(storageUnit.getStoredItemType(), stack)) {
                size = Math.min(size, storageUnit.getStoredItemType().stackSize);

                ItemStack took = ItemHandlerHelper.copyStackWithSize(storageUnit.getStoredItemType(), size);

                storageUnit.setStoredItemCount(storageUnit.getStoredItemType().stackSize - size);

                return took;
            }
        } else {
            IItemHandler handler = getItemHandler();

            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack slot = handler.getStackInSlot(i);

                    if (slot != null && RefinedStorageUtils.compareStack(slot, stack, flags)) {
                        size = Math.min(size, slot.stackSize);

                        ItemStack took = ItemHandlerHelper.copyStackWithSize(slot, size);

                        handler.extractItem(i, size, false);

                        return took;
                    }
                }
            }
        }

        return null;
    }

    /*
    @Override
    public boolean mayPush(ItemStack stack) {
        if (ModeFilter.respectsMode(filters, this, compare, stack)) {
            IDeepStorageUnit storageUnit = getStorageUnit();

            if (storageUnit != null) {
                if (storageUnit.getStoredItemType() == null) {
                    return stack.stackSize < storageUnit.getMaxStoredCount();
                }

                return RefinedStorageUtils.compareStackNoQuantity(storageUnit.getStoredItemType(), stack) && (storageUnit.getStoredItemType().stackSize + stack.stackSize) < storageUnit.getMaxStoredCount();
            } else {
                IItemHandler handler = getItemHandler();

                if (handler != null) {
                    return ItemHandlerHelper.insertItem(handler, stack, true) == null;
                }
            }
        }

        return false;
    }
    */

    public IDeepStorageUnit getStorageUnit() {
        return getFacingTile() instanceof IDeepStorageUnit ? (IDeepStorageUnit) getFacingTile() : null;
    }

    public IItemHandler getItemHandler() {
        return RefinedStorageUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(priority);

        buf.writeInt(getStored());

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

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
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(filters, 0, nbt);

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
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
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
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
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
        if (worldObj.isRemote) {
            return stored;
        }

        IDeepStorageUnit storageUnit = getStorageUnit();

        if (storageUnit != null) {
            return storageUnit.getStoredItemType() == null ? 0 : storageUnit.getStoredItemType().stackSize;
        } else {
            IItemHandler handler = getItemHandler();

            if (handler != null) {
                int size = 0;

                for (int i = 0; i < handler.getSlots(); ++i) {
                    if (handler.getStackInSlot(i) != null) {
                        size += handler.getStackInSlot(i).stackSize;
                    }
                }

                return size;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int getCapacity() {
        IDeepStorageUnit storageUnit = getStorageUnit();

        if (storageUnit != null) {
            return storageUnit.getMaxStoredCount();
        } else {
            IItemHandler handler = getItemHandler();

            if (handler != null) {
                return handler.getSlots() * 64;
            }
        }

        return 0;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == RefinedStorageCapabilities.STORAGE_PROVIDER_CAPABILITY) {
            return (T) this;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == RefinedStorageCapabilities.STORAGE_PROVIDER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

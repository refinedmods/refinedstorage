package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import com.raoulvdberge.refinedstorage.block.BlockStorage;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class TileStorage extends TileNode implements IItemStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable, IExcessVoidable, IAccessType {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<AccessType> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Integer> STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileStorage>() {
        @Override
        public Integer getValue(TileStorage tile) {
            return ItemStorageNBT.getStoredFromNBT(tile.storageTag);
        }
    });
    public static final TileDataParameter<Boolean> VOID_EXCESS = IExcessVoidable.createParameter();

    class ItemStorage extends ItemStorageNBT {
        public ItemStorage() {
            super(TileStorage.this.getStorageTag(), TileStorage.this.getCapacity(), TileStorage.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(filters, mode, compare, stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            ItemStack result = super.insertItem(stack, size, simulate);

            if (voidExcess && result != null) {
                // Simulate should not matter as the items are voided anyway
                result.stackSize = -result.stackSize;
            }

            return result;
        }

        @Override
        public AccessType getAccessType() {
            return accessType;
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_VOID_EXCESS = "VoidExcess";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);

    private NBTTagCompound storageTag = ItemStorageNBT.createNBT();

    private ItemStorage storage;

    private EnumItemStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private boolean voidExcess = false;

    public TileStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(VOID_EXCESS);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.storageUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new ItemStorage();

            if (network != null) {
                network.getItemStorageCache().invalidate();
            }
        }
    }

    public void onBreak() {
        if (storage != null) {
            storage.writeToNBT();
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getItemStorageCache().invalidate();
    }

    @Override
    public void addItemStorages(List<IItemStorage> storages) {
        if (storage != null) {
            storages.add(storage);
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_STORAGE)) {
            storageTag = tag.getCompoundTag(NBT_STORAGE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        if (storage != null) {
            storage.writeToNBT();
        }

        tag.setTag(NBT_STORAGE, storageTag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        RSUtils.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setBoolean(NBT_VOID_EXCESS, voidExcess);

        RSUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        RSUtils.readItems(filters, 0, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_VOID_EXCESS)) {
            voidExcess = tag.getBoolean(NBT_VOID_EXCESS);
        }

        accessType = RSUtils.readAccessType(tag);
    }

    public EnumItemStorageType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RSBlocks.STORAGE) {
            this.type = ((EnumItemStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
        }

        return type == null ? EnumItemStorageType.TYPE_1K : type;
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
    public boolean getVoidExcess() {
        return voidExcess;
    }

    @Override
    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;

        markDirty();
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:storage." + getType().getId() + ".name";
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return null;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return PRIORITY;
    }

    @Override
    public TileDataParameter<Boolean> getVoidExcessParameter() {
        return VOID_EXCESS;
    }

    @Override
    public TileDataParameter<AccessType> getAccessTypeParameter() {
        return ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return "items";
    }

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public ItemStorageNBT getStorage() {
        return storage;
    }

    public ItemHandlerBasic getFilters() {
        return filters;
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType value) {
        this.accessType = value;

        if (network != null) {
            network.getItemStorageCache().invalidate();
        }

        markDirty();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    @Override
    public int getStored() {
        return STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity();
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageItemNBT;
import com.raoulvdberge.refinedstorage.block.BlockStorage;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.tile.IStorageGui;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class NetworkNodeStorage extends NetworkNode implements IStorageGui, IStorageProvider, IComparable, IFilterable, IPrioritizable, IExcessVoidable, IAccessType {
    class StorageItem extends StorageItemNBT {
        public StorageItem() {
            super(NetworkNodeStorage.this.getStorageTag(), NetworkNodeStorage.this.getCapacity(), NetworkNodeStorage.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(filters, mode, compare, stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insert(stack, size, simulate);
        }

        @Override
        public AccessType getAccessType() {
            return accessType;
        }

        @Override
        public boolean isVoiding() {
            return voidExcess;
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_VOID_EXCESS = "VoidExcess";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, new ItemHandlerListenerNetworkNode(this));

    private NBTTagCompound storageTag = StorageItemNBT.createNBT();

    private StorageItem storage;

    private EnumItemStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private boolean voidExcess = false;

    public NetworkNodeStorage(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.storageUsage;
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new StorageItem();

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
    public void onConnectedStateChange(INetworkMaster network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getItemStorageCache().invalidate();
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        if (storage != null) {
            storages.add(storage);
        }
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        // NO OP
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
        if (type == null && holder.world().getBlockState(holder.pos()).getBlock() == RSBlocks.STORAGE) {
            type = ((EnumItemStorageType) holder.world().getBlockState(holder.pos()).getValue(BlockStorage.TYPE));
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

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public StorageItemNBT getStorage() {
        return storage;
    }

    public ItemHandlerBasic getFilters() {
        return filters;
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
        return TileStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return TileStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return TileStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return TileStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<Boolean> getVoidExcessParameter() {
        return TileStorage.VOID_EXCESS;
    }

    @Override
    public TileDataParameter<AccessType> getAccessTypeParameter() {
        return TileStorage.ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return "items";
    }

    @Override
    public int getStored() {
        return TileStorage.STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity();
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
}

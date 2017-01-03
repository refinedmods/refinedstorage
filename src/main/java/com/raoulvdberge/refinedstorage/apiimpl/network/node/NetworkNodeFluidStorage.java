package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.block.BlockFluidStorage;
import com.raoulvdberge.refinedstorage.block.EnumFluidStorageType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.IStorageGui;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NetworkNodeFluidStorage extends NetworkNode implements IStorageGui, IStorageProvider, IComparable, IFilterable, IPrioritizable, IExcessVoidable, IAccessType {
    public static final String ID = "fluid_storage";

    class StorageFluid extends StorageDiskFluid {
        public StorageFluid() {
            super(NetworkNodeFluidStorage.this.getStorageTag(), NetworkNodeFluidStorage.this.getCapacity());
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        @Nullable
        public FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(filters, mode, compare, stack)) {
                return RSUtils.copyStackWithSize(stack, size);
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

        @Override
        public void onChanged() {
            super.onChanged();

            markDirty();
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_VOID_EXCESS = "VoidExcess";

    private ItemHandlerFluid filters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

    private NBTTagCompound storageTag = StorageDiskFluid.getTag();

    private StorageFluid storage;

    private EnumFluidStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IFilterable.WHITELIST;
    private boolean voidExcess = false;

    public NetworkNodeFluidStorage(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.fluidStorageUsage;
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new StorageFluid();

            if (network != null) {
                network.getFluidStorageCache().invalidate();
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

        network.getFluidStorageCache().invalidate();
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        // NO OP
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
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
    public String getId() {
        return ID;
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

    public EnumFluidStorageType getType() {
        if (type == null && holder.world().getBlockState(holder.pos()).getBlock() == RSBlocks.FLUID_STORAGE) {
            type = (EnumFluidStorageType) holder.world().getBlockState(holder.pos()).getValue(BlockFluidStorage.TYPE);
        }

        return type == null ? EnumFluidStorageType.TYPE_64K : type;
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

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public StorageDiskFluid getStorage() {
        return storage;
    }

    public ItemHandlerFluid getFilters() {
        return filters;
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:fluid_storage." + getType().getId() + ".name";
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return null;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return TileFluidStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return TileFluidStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return TileFluidStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return TileFluidStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<Boolean> getVoidExcessParameter() {
        return TileFluidStorage.VOID_EXCESS;
    }

    @Override
    public TileDataParameter<AccessType> getAccessTypeParameter() {
        return TileFluidStorage.ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return "fluids";
    }

    @Override
    public int getStored() {
        return TileFluidStorage.STORED.getValue();
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
            network.getFluidStorageCache().invalidate();
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
    public boolean getVoidExcess() {
        return voidExcess;
    }

    @Override
    public void setVoidExcess(boolean value) {
        this.voidExcess = value;

        markDirty();
    }
}

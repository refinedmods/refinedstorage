package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.block.BlockFluidStorage;
import com.raoulvdberge.refinedstorage.block.FluidStorageType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NetworkNodeFluidStorage extends NetworkNode implements IGuiStorage, IStorageProvider, IComparable, IFilterable, IPrioritizable, IExcessVoidable, IAccessType {
    public static final String ID = "fluid_storage";

    class StorageFluid extends StorageDiskFluid {
        StorageFluid(NBTTagCompound tag) {
            super(tag, NetworkNodeFluidStorage.this.getCapacity());

            this.onPassContainerContext(
                NetworkNodeFluidStorage.this::markDirty,
                NetworkNodeFluidStorage.this::getVoidExcess,
                NetworkNodeFluidStorage.this::getAccessType
            );
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        @Nullable
        public FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(filters, mode, compare, stack)) {
                return StackUtils.copy(stack, size);
            }

            return super.insert(stack, size, simulate);
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_VOID_EXCESS = "VoidExcess";

    private ItemHandlerFluid filters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

    private StorageFluid storage = new StorageFluid(StorageDiskFluid.getTag());
    private NBTTagCompound storageTagToRead;

    private FluidStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IFilterable.WHITELIST;
    private boolean voidExcess = false;

    public NetworkNodeFluidStorage(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.fluidStorageUsage;
    }

    @Override
    public void update() {
        super.update();

        if (storageTagToRead != null) {
            storage = new StorageFluid(storageTagToRead);

            storage.readFromNBT();

            if (network != null) {
                network.getFluidStorageCache().invalidate();
            }

            storageTagToRead = null;
        }
    }

    public void onPlacedWithStorage(NBTTagCompound tag) {
        storageTagToRead = tag;
    }

    public void onBreak() {
        if (storage != null) {
            storage.writeToNBT();
        }
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getNodeGraph().schedulePostRebuildAction(StorageCacheFluid.INVALIDATE);
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
            storageTagToRead = tag.getCompoundTag(NBT_STORAGE);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        storage.writeToNBT();

        tag.setTag(NBT_STORAGE, storage.getStorageTag());

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setBoolean(NBT_VOID_EXCESS, voidExcess);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(filters, 0, tag);

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

        accessType = AccessTypeUtils.readAccessType(tag);
    }

    public FluidStorageType getType() {
        if (type == null && world != null && world.getBlockState(pos).getBlock() == RSBlocks.FLUID_STORAGE) {
            type = (FluidStorageType) world.getBlockState(pos).getValue(BlockFluidStorage.TYPE);
        }

        return type == null ? FluidStorageType.TYPE_64K : type;
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
    public TileDataParameter<Integer, ?> getTypeParameter() {
        return null;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileFluidStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter() {
        return TileFluidStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getFilterParameter() {
        return TileFluidStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return TileFluidStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<Boolean, ?> getVoidExcessParameter() {
        return TileFluidStorage.VOID_EXCESS;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
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

        if (network != null) {
            network.getFluidStorageCache().sort();
        }
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

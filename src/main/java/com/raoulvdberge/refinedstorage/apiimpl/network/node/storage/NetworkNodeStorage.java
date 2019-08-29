package com.raoulvdberge.refinedstorage.apiimpl.network.node.storage;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.block.BlockStorage;
import com.raoulvdberge.refinedstorage.block.enums.ItemStorageType;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import com.raoulvdberge.refinedstorage.tile.config.IAccessType;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IPrioritizable;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.UUID;

public class NetworkNodeStorage extends NetworkNode implements IGuiStorage, IStorageProvider, IComparable, IFilterable, IPrioritizable, IAccessType, IStorageDiskContainerContext {
    public static final String ID = "storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    public static final String NBT_ID = "Id";

    private ItemHandlerBase filters = new ItemHandlerBase(9, new ListenerNetworkNode(this));

    private ItemStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;

    private UUID storageId = UUID.randomUUID();
    private IStorageDisk<ItemStack> storage;

    public NetworkNodeStorage(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.storageUsage;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getNodeGraph().runActionWhenPossible(StorageCacheItem.INVALIDATE);
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        if (storage == null) {
            loadStorage();
        }

        storages.add(storage);
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        // NO OP
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.setUniqueId(NBT_ID, storageId);

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.hasUniqueId(NBT_ID)) {
            storageId = tag.getUniqueId(NBT_ID);

            loadStorage();
        }

        OneSixMigrationHelper.migrateItemStorageBlock(this, tag);
    }

    public void loadStorage() {
        IStorageDisk disk = API.instance().getStorageDiskManager(world).get(storageId);

        if (disk == null) {
            API.instance().getStorageDiskManager(world).set(storageId, disk = API.instance().createDefaultItemDisk(world, getType().getCapacity()));
            API.instance().getStorageDiskManager(world).markForSaving();
        }

        this.storage = new StorageDiskItemStorageWrapper(this, disk);
    }

    public void setStorageId(UUID id) {
        this.storageId = id;

        markDirty();
    }

    public UUID getStorageId() {
        return storageId;
    }

    public IStorageDisk<ItemStack> getStorage() {
        return storage;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(filters, 0, tag);

        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
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

        accessType = AccessTypeUtils.readAccessType(tag);

        OneSixMigrationHelper.migrateEmptyWhitelistToEmptyBlacklist(version, this, filters);
    }

    public ItemStorageType getType() {
        if (type == null && world != null) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == RSBlocks.STORAGE) {
                type = (ItemStorageType) state.getValue(BlockStorage.TYPE);
            }
        }

        return type == null ? ItemStorageType.TYPE_1K : type;
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

    public ItemHandlerBase getFilters() {
        return filters;
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:storage." + getType().getId() + ".name";
    }

    @Override
    public TileDataParameter<Integer, ?> getTypeParameter() {
        return null;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter() {
        return TileStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getFilterParameter() {
        return TileStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return TileStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return TileStorage.ACCESS_TYPE;
    }

    @Override
    public long getStored() {
        return TileStorage.STORED.getValue();
    }

    @Override
    public long getCapacity() {
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

        if (network != null) {
            network.getItemStorageCache().sort();
        }
    }
}

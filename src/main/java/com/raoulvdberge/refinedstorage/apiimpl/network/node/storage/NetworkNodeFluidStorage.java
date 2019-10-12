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
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IStorageScreen;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.raoulvdberge.refinedstorage.block.BlockFluidStorage;
import com.raoulvdberge.refinedstorage.block.enums.FluidStorageType;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import com.raoulvdberge.refinedstorage.tile.config.IAccessType;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IPrioritizable;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.UUID;

public class NetworkNodeFluidStorage extends NetworkNode implements IStorageScreen, IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IAccessType, IStorageDiskContainerContext {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid_storage");

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_FILTERS = "Filters";
    public static final String NBT_ID = "Id";

    private FluidInventory filters = new FluidInventory(9, new NetworkNodeListener(this));

    private FluidStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;

    private UUID storageId = UUID.randomUUID();
    private IStorageDisk<FluidStack> storage;

    public NetworkNodeFluidStorage(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.fluidStorageUsage;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getNodeGraph().runActionWhenPossible(FluidStorageCache.INVALIDATE);
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        // NO OP
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        if (storage == null) {
            loadStorage();
        }

        storages.add(storage);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putUniqueId(NBT_ID, storageId);

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.hasUniqueId(NBT_ID)) {
            storageId = tag.getUniqueId(NBT_ID);

            loadStorage();
        }
    }

    public void loadStorage() {
        IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) world).get(storageId);

        if (disk == null) {
            API.instance().getStorageDiskManager((ServerWorld) world).set(storageId, disk = API.instance().createDefaultFluidDisk((ServerWorld) world, getType().getCapacity()));
            API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
        }

        this.storage = new StorageDiskFluidStorageWrapper(this, disk);
    }

    public void setStorageId(UUID id) {
        this.storageId = id;

        markDirty();
    }

    public UUID getStorageId() {
        return storageId;
    }

    public IStorageDisk<FluidStack> getStorage() {
        return storage;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.put(NBT_FILTERS, filters.writeToNbt());
        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_FILTERS)) {
            filters.readFromNbt(tag.getCompound(NBT_FILTERS));
        }

        if (tag.contains(NBT_PRIORITY)) {
            priority = tag.getInt(NBT_PRIORITY);
        }

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        accessType = AccessTypeUtils.readAccessType(tag);
    }

    public FluidStorageType getType() {
        if (type == null && world != null) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() == RSBlocks.FLUID_STORAGE) {
                type = state.get(BlockFluidStorage.TYPE);
            }
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
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    public FluidInventory getFilters() {
        return filters;
    }

    @Override
    public ITextComponent getTitle() {
        return new TranslationTextComponent("block.refinedstorage:fluid_storage." + getType().getId() + ".name");
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
    public TileDataParameter<Integer, ?> getWhitelistBlacklistParameter() {
        return TileFluidStorage.WHITELIST_BLACKLIST;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return TileFluidStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return TileFluidStorage.ACCESS_TYPE;
    }

    @Override
    public long getStored() {
        return TileFluidStorage.STORED.getValue();
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
}

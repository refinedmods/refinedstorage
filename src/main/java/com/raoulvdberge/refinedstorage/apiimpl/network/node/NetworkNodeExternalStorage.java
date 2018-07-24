package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkNodeExternalStorage extends NetworkNode implements IStorageProvider, IGuiStorage, IComparable, IFilterable, IPrioritizable, IType, IAccessType, IExternalStorageContext, ICoverable {
    public static final String ID = "external_storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_COVERS = "Covers";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;
    private int type = IType.ITEMS;
    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int networkTicks;

    private CoverManager coverManager = new CoverManager(this);

    private List<IStorageExternal<ItemStack>> itemStorages = new CopyOnWriteArrayList<>();
    private List<IStorageExternal<FluidStack>> fluidStorages = new CopyOnWriteArrayList<>();

    public NetworkNodeExternalStorage(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.externalStorageUsage + ((itemStorages.size() + fluidStorages.size()) * RS.INSTANCE.config.externalStoragePerStorageUsage);
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        updateStorage(network);
    }

    @Override
    public void update() {
        super.update();

        if (network != null) {
            if (networkTicks++ == 0) {
                updateStorage(network);

                return;
            }

            for (IStorageExternal<ItemStack> storage : itemStorages) {
                storage.update(network);
            }

            for (IStorageExternal<FluidStack> storage : fluidStorages) {
                storage.update(network);
            }
        }
    }

    @Override
    protected void onDirectionChanged() {
        super.onDirectionChanged();

        if (network != null) {
            updateStorage(network);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getTagList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setTag(NBT_COVERS, coverManager.writeToNbt());

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
        }

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        accessType = AccessTypeUtils.readAccessType(tag);

        OneSixMigrationHelper.migrateEmptyWhitelistToEmptyBlacklist(version, this, itemFilters);
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

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();

        if (network != null) {
            network.getItemStorageCache().sort();
            network.getFluidStorageCache().sort();
        }
    }

    public void updateStorage(INetwork network) {
        itemStorages.clear();
        fluidStorages.clear();

        TileEntity facing = getFacingTile();

        if (facing != null) {
            if (type == IType.ITEMS) {
                for (IExternalStorageProvider provider : API.instance().getExternalStorageProviders(StorageType.ITEM)) {
                    if (provider.canProvide(facing, getDirection())) {
                        itemStorages.add(provider.provide(this, () -> getFacingTile(), getDirection()));
                    }
                }
            } else if (type == IType.FLUIDS) {
                for (IExternalStorageProvider provider : API.instance().getExternalStorageProviders(StorageType.FLUID)) {
                    if (provider.canProvide(facing, getDirection())) {
                        fluidStorages.add(provider.provide(this, () -> getFacingTile(), getDirection()));
                    }
                }
            }
        }

        network.getNodeGraph().addPostRebuildHandler(StorageCacheItem.INVALIDATE);
        network.getNodeGraph().addPostRebuildHandler(StorageCacheFluid.INVALIDATE);
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        storages.addAll(this.itemStorages);
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        storages.addAll(this.fluidStorages);
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:external_storage";
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileExternalStorage.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter() {
        return TileExternalStorage.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getFilterParameter() {
        return TileExternalStorage.MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return TileExternalStorage.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return TileExternalStorage.ACCESS_TYPE;
    }

    @Override
    public int getStored() {
        return TileExternalStorage.STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return TileExternalStorage.CAPACITY.getValue();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public boolean acceptsItem(ItemStack stack) {
        return IFilterable.acceptsItem(itemFilters, mode, compare, stack);
    }

    @Override
    public boolean acceptsFluid(FluidStack stack) {
        return IFilterable.acceptsFluid(fluidFilters, mode, compare, stack);
    }

    @Override
    public void setAccessType(AccessType type) {
        this.accessType = type;

        if (network != null) {
            network.getItemStorageCache().invalidate();
            network.getFluidStorageCache().invalidate();
        }

        markDirty();
    }

    @Override
    public TileDataParameter<Integer, ?> getTypeParameter() {
        return TileExternalStorage.TYPE;
    }

    @Override
    public int getType() {
        return world.isRemote ? TileExternalStorage.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();

        if (network != null) {
            updateStorage(network);
        }
    }

    @Override
    public IItemHandler getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    public List<IStorageExternal<ItemStack>> getItemStorages() {
        return itemStorages;
    }

    public List<IStorageExternal<FluidStack>> getFluidStorages() {
        return fluidStorages;
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        return coverManager.canConduct(direction);
    }

    @Nullable
    @Override
    public IItemHandler getDrops() {
        return coverManager.getAsInventory();
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}

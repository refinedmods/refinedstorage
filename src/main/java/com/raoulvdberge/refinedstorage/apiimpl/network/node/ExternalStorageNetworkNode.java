package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.raoulvdberge.refinedstorage.tile.ExternalStorageTile;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExternalStorageNetworkNode extends NetworkNode implements IStorageProvider, IStorageScreen, IComparable, IWhitelistBlacklist, IPrioritizable, IType, IAccessType, IExternalStorageContext {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "external_storage");

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private BaseItemHandler itemFilters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private FluidInventory fluidFilters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));

    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int networkTicks;

    private List<IExternalStorage<ItemStack>> itemStorages = new CopyOnWriteArrayList<>();
    private List<IExternalStorage<FluidStack>> fluidStorages = new CopyOnWriteArrayList<>();

    public ExternalStorageNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getExternalStorage().getUsage();
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        updateStorage(network);
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate()) {
            if (networkTicks++ == 0) {
                updateStorage(network);

                return;
            }

            for (IExternalStorage<ItemStack> storage : itemStorages) {
                storage.update(network);
            }

            for (IExternalStorage<FluidStack> storage : fluidStorages) {
                storage.update(network);
            }
        }
    }

    @Override
    public void onDirectionChanged(Direction direction) {
        super.onDirectionChanged(direction);

        if (network != null) {
            updateStorage(network);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
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

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        accessType = AccessTypeUtils.readAccessType(tag);
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

                        break;
                    }
                }
            } else if (type == IType.FLUIDS) {
                for (IExternalStorageProvider provider : API.instance().getExternalStorageProviders(StorageType.FLUID)) {
                    if (provider.canProvide(facing, getDirection())) {
                        fluidStorages.add(provider.provide(this, () -> getFacingTile(), getDirection()));

                        break;
                    }
                }
            }
        }

        network.getNodeGraph().runActionWhenPossible(ItemStorageCache.INVALIDATE);
        network.getNodeGraph().runActionWhenPossible(FluidStorageCache.INVALIDATE);
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
    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui.refinedstorage:external_storage");
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return ExternalStorageTile.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter() {
        return ExternalStorageTile.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getWhitelistBlacklistParameter() {
        return ExternalStorageTile.WHITELIST_BLACKLIST;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return ExternalStorageTile.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return ExternalStorageTile.ACCESS_TYPE;
    }

    @Override
    public long getStored() {
        return ExternalStorageTile.STORED.getValue();
    }

    @Override
    public long getCapacity() {
        return ExternalStorageTile.CAPACITY.getValue();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public boolean acceptsItem(ItemStack stack) {
        return IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, stack);
    }

    @Override
    public boolean acceptsFluid(FluidStack stack) {
        return IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack);
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
        return ExternalStorageTile.TYPE;
    }

    @Override
    public int getType() {
        return world.isRemote ? ExternalStorageTile.TYPE.getValue() : type;
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
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    public List<IExternalStorage<ItemStack>> getItemStorages() {
        return itemStorages;
    }

    public List<IExternalStorage<FluidStack>> getFluidStorages() {
        return fluidStorages;
    }
}

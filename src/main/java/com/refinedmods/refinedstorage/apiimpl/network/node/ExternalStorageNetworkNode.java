package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.blockentity.ExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.*;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExternalStorageNetworkNode extends NetworkNode implements IStorageProvider, IStorageScreen, IComparable, IWhitelistBlacklist, IPrioritizable, IType, IAccessType, IExternalStorageContext, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "external_storage");

    private static final Logger LOGGER = LogManager.getLogger(ExternalStorageNetworkNode.class);

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private final BaseItemHandler itemFilters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));
    private final List<IExternalStorage<ItemStack>> itemStorages = new CopyOnWriteArrayList<>();
    private final List<IExternalStorage<FluidStack>> fluidStorages = new CopyOnWriteArrayList<>();
    private final CoverManager coverManager;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int networkTicks;

    public ExternalStorageNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
        this.coverManager = new CoverManager(this);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getExternalStorage().getUsage();
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        LOGGER.debug("Connectivity state of external storage at {} changed to {} due to {}", pos, state, cause);

        updateStorage(network, InvalidateCause.CONNECTED_STATE_CHANGED);
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && level.isLoaded(pos)) {
            if (networkTicks++ == 0) {
                updateStorage(network, InvalidateCause.INITIAL_TICK_INVALIDATION);

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
            updateStorage(network, InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
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
    public void readConfiguration(CompoundTag tag) {
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

    public void updateStorage(INetwork network, InvalidateCause cause) {
        itemStorages.clear();
        fluidStorages.clear();

        BlockEntity facing = getFacingBlockEntity();

        if (facing != null) {
            if (type == IType.ITEMS) {
                for (IExternalStorageProvider<ItemStack> provider : API.instance().<ItemStack>getExternalStorageProviders(StorageType.ITEM)) {
                    if (provider.canProvide(facing, getDirection())) {
                        itemStorages.add(provider.provide(this, getFacingBlockEntity(), getDirection()));

                        break;
                    }
                }
            } else if (type == IType.FLUIDS) {
                for (IExternalStorageProvider<FluidStack> provider : API.instance().<FluidStack>getExternalStorageProviders(StorageType.FLUID)) {
                    if (provider.canProvide(facing, getDirection())) {
                        fluidStorages.add(provider.provide(this, getFacingBlockEntity(), getDirection()));

                        break;
                    }
                }
            }
        }

        network.getNodeGraph().runActionWhenPossible(ItemStorageCache.INVALIDATE_ACTION.apply(cause));
        network.getNodeGraph().runActionWhenPossible(FluidStorageCache.INVALIDATE_ACTION.apply(cause));
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
    public Component getTitle() {
        return Component.translatable("gui.refinedstorage:external_storage");
    }

    @Override
    public long getStored() {
        return ExternalStorageBlockEntity.STORED.getValue();
    }

    @Override
    public long getCapacity() {
        return ExternalStorageBlockEntity.CAPACITY.getValue();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType type) {
        this.accessType = type;

        if (network != null) {
            network.getItemStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
            network.getFluidStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
        }

        markDirty();
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
    public int getType() {
        return level.isClientSide ? ExternalStorageBlockEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();

        if (network != null) {
            updateStorage(network, InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
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

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(CoverManager.NBT_COVER_MANAGER)) {
            this.coverManager.readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));
        }
    }


    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.coverManager.writeToNbt());

        return tag;
    }
}

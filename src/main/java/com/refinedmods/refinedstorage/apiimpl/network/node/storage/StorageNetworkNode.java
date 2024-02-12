package com.refinedmods.refinedstorage.apiimpl.network.node.storage;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.IStorageProvider;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.IStorageScreen;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IAccessType;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IPrioritizable;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class StorageNetworkNode extends NetworkNode implements IStorageScreen, IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IAccessType, IStorageDiskContainerContext {
    public static final ResourceLocation ONE_K_STORAGE_BLOCK_ID = new ResourceLocation(RS.ID, "1k_storage_block");
    public static final ResourceLocation FOUR_K_STORAGE_BLOCK_ID = new ResourceLocation(RS.ID, "4k_storage_block");
    public static final ResourceLocation SIXTEEN_K_STORAGE_BLOCK_ID = new ResourceLocation(RS.ID, "16k_storage_block");
    public static final ResourceLocation SIXTY_FOUR_K_STORAGE_BLOCK_ID = new ResourceLocation(RS.ID, "64k_storage_block");
    public static final ResourceLocation CREATIVE_STORAGE_BLOCK_ID = new ResourceLocation(RS.ID, "creative_storage_block");
    public static final String NBT_ID = "Id";
    private static final Logger LOGGER = LogManager.getLogger(StorageNetworkNode.class);
    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private final BaseItemHandler filters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));

    private final ItemStorageType type;

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;

    private UUID storageId = UUID.randomUUID();
    private IStorageDisk<ItemStack> storage;

    public StorageNetworkNode(Level level, BlockPos pos, ItemStorageType type) {
        super(level, pos);

        this.type = type;
    }

    public static ResourceLocation getId(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return ONE_K_STORAGE_BLOCK_ID;
            case FOUR_K:
                return FOUR_K_STORAGE_BLOCK_ID;
            case SIXTEEN_K:
                return SIXTEEN_K_STORAGE_BLOCK_ID;
            case SIXTY_FOUR_K:
                return SIXTY_FOUR_K_STORAGE_BLOCK_ID;
            case CREATIVE:
                return CREATIVE_STORAGE_BLOCK_ID;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }

    @Override
    public int getEnergyUsage() {
        switch (type) {
            case ONE_K:
                return RS.SERVER_CONFIG.getStorageBlock().getOneKUsage();
            case FOUR_K:
                return RS.SERVER_CONFIG.getStorageBlock().getFourKUsage();
            case SIXTEEN_K:
                return RS.SERVER_CONFIG.getStorageBlock().getSixteenKUsage();
            case SIXTY_FOUR_K:
                return RS.SERVER_CONFIG.getStorageBlock().getSixtyFourKUsage();
            case CREATIVE:
                return RS.SERVER_CONFIG.getStorageBlock().getCreativeUsage();
            default:
                return 0;
        }
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        LOGGER.debug("Connectivity state of item storage block at {} changed to {} due to {}", pos, state, cause);

        network.getNodeGraph().runActionWhenPossible(ItemStorageCache.INVALIDATE_ACTION.apply(InvalidateCause.CONNECTED_STATE_CHANGED));
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        if (storage == null) {
            loadStorage(null);
        }

        storages.add(storage);
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        // NO OP
    }

    @Override
    public ResourceLocation getId() {
        return getId(type);
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.putUUID(NBT_ID, storageId);

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.hasUUID(NBT_ID)) {
            storageId = tag.getUUID(NBT_ID);

            loadStorage(null);
        }
    }

    public void loadStorage(@Nullable Player owner) {
        IStorageDisk disk = API.instance().getStorageDiskManager((ServerLevel) level).get(storageId);

        if (disk == null) {
            disk = API.instance().createDefaultItemDisk((ServerLevel) level, type.getCapacity(), owner);

            API.instance().getStorageDiskManager((ServerLevel) level).set(storageId, disk);
            API.instance().getStorageDiskManager((ServerLevel) level).markForSaving();
        }

        this.storage = new ItemStorageWrapperStorageDisk(this, disk);
    }

    public UUID getStorageId() {
        return storageId;
    }

    public void setStorageId(UUID id) {
        this.storageId = id;

        markDirty();
    }

    public IStorageDisk<ItemStack> getStorage() {
        return storage;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(filters, 0, tag);

        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(filters, 0, tag);

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

    public BaseItemHandler getFilters() {
        return filters;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.refinedstorage." + type.getName() + "_storage_block");
    }

    @Override
    public long getStored() {
        return StorageBlockEntity.STORED.getValue();
    }

    @Override
    public long getCapacity() {
        return type.getCapacity();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType value) {
        this.accessType = value;

        if (network != null) {
            network.getItemStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED);
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

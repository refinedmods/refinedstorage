package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.*;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class DiskDriveBlockEntity extends NetworkNodeBlockEntity<DiskDriveNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, DiskDriveBlockEntity> PRIORITY = IPrioritizable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, DiskDriveBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, DiskDriveBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, DiskDriveBlockEntity> TYPE = IType.createParameter();
    public static final BlockEntitySynchronizationParameter<AccessType, DiskDriveBlockEntity> ACCESS_TYPE = IAccessType.createParameter();
    public static final BlockEntitySynchronizationParameter<Long, DiskDriveBlockEntity> STORED = new BlockEntitySynchronizationParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long stored = 0;

        for (IStorageDisk storage : t.getNode().getItemDisks()) {
            if (storage != null) {
                stored += storage.getStored();
            }
        }

        for (IStorageDisk storage : t.getNode().getFluidDisks()) {
            if (storage != null) {
                stored += storage.getStored();
            }
        }

        return stored;
    });
    public static final BlockEntitySynchronizationParameter<Long, DiskDriveBlockEntity> CAPACITY = new BlockEntitySynchronizationParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long capacity = 0;

        for (IStorageDisk storage : t.getNode().getItemDisks()) {
            if (storage != null) {
                if (storage.getCapacity() == -1) {
                    return -1L;
                }

                capacity += storage.getCapacity();
            }
        }

        for (IStorageDisk storage : t.getNode().getFluidDisks()) {
            if (storage != null) {
                if (storage.getCapacity() == -1) {
                    return -1L;
                }

                capacity += storage.getCapacity();
            }
        }

        return capacity;
    });

    public static final ModelProperty<DiskState[]> DISK_STATE_PROPERTY = new ModelProperty<>();

    private static final String NBT_DISK_STATE = "DiskStates";

    private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getDisks());

    private final DiskState[] diskState = new DiskState[8];

    public DiskDriveBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.DISK_DRIVE, pos, state);

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(ACCESS_TYPE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);

        Arrays.fill(diskState, DiskState.NONE);
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        ListTag list = new ListTag();

        for (DiskState state : getNode().getDiskState()) {
            list.add(IntTag.valueOf(state.ordinal()));
        }

        tag.put(NBT_DISK_STATE, list);

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        ListTag list = tag.getList(NBT_DISK_STATE, Tag.TAG_INT);

        for (int i = 0; i < list.size(); ++i) {
            diskState[i] = DiskState.values()[list.getInt(i)];
        }

        requestModelDataUpdate();

        LevelUtils.updateBlock(level, worldPosition);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(DISK_STATE_PROPERTY, diskState).build();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return diskCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    @Override
    @Nonnull
    public DiskDriveNetworkNode createNode(Level level, BlockPos pos) {
        return new DiskDriveNetworkNode(level, pos);
    }
}

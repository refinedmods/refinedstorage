package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class DiskManipulatorTile extends NetworkNodeTile<DiskManipulatorNetworkNode> {
    public static final TileDataParameter<Integer, DiskManipulatorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, DiskManipulatorTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, DiskManipulatorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, DiskManipulatorTile> IO_MODE = new TileDataParameter<>(DataSerializers.INT, DiskManipulatorNetworkNode.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
        t.getNode().setIoMode(v);
        t.getNode().markDirty();
    });

    public static final ModelProperty<DiskState[]> DISK_STATE_PROPERTY = new ModelProperty<>();

    private static final String NBT_DISK_STATE = "DiskStates";

    private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getDisks());

    private final DiskState[] diskState = new DiskState[6];

    public DiskManipulatorTile() {
        super(RSTiles.DISK_MANIPULATOR);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

        Arrays.fill(diskState, DiskState.NONE);
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        ListNBT list = new ListNBT();

        for (DiskState state : getNode().getDiskState()) {
            list.add(IntNBT.valueOf(state.ordinal()));
        }

        tag.put(NBT_DISK_STATE, list);

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        ListNBT list = tag.getList(NBT_DISK_STATE, Constants.NBT.TAG_INT);

        for (int i = 0; i < list.size(); ++i) {
            diskState[i] = DiskState.values()[list.getInt(i)];
        }

        requestModelDataUpdate();

        WorldUtils.updateBlock(level, worldPosition);
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
    public DiskManipulatorNetworkNode createNode(World world, BlockPos pos) {
        return new DiskManipulatorNetworkNode(world, pos);
    }
}

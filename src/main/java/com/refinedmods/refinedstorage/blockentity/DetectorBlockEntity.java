package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.DetectorScreen;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class DetectorBlockEntity extends NetworkNodeBlockEntity<DetectorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, DetectorBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, DetectorBlockEntity> TYPE = IType.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, DetectorBlockEntity> MODE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getMode(), (t, v) -> {
        if (v == DetectorNetworkNode.MODE_UNDER || v == DetectorNetworkNode.MODE_EQUAL || v == DetectorNetworkNode.MODE_ABOVE) {
            t.getNode().setMode(v);
            t.getNode().markDirty();
        }
    });
    public static final BlockEntitySynchronizationParameter<Integer, DetectorBlockEntity> AMOUNT = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getAmount(), (t, v) -> {
        t.getNode().setAmount(v);
        t.getNode().markDirty();
    }, (initial, value) -> BaseScreen.executeLater(DetectorScreen.class, detectorScreen -> detectorScreen.updateAmountField(value)));

    private static final String NBT_POWERED = "Powered";

    public DetectorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.DETECTOR, pos, state);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        getNode().setPowered(tag.getBoolean(NBT_POWERED));

        super.readUpdate(tag);
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        tag.putBoolean(NBT_POWERED, getNode().isPowered());

        return tag;
    }

    @Override
    @Nonnull
    public DetectorNetworkNode createNode(Level level, BlockPos pos) {
        return new DetectorNetworkNode(level, pos);
    }
}

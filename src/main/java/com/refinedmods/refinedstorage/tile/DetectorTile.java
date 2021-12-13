package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.DetectorScreen;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class DetectorTile extends NetworkNodeTile<DetectorNetworkNode> {
    public static final TileDataParameter<Integer, DetectorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, DetectorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, DetectorTile> MODE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getMode(), (t, v) -> {
        if (v == DetectorNetworkNode.MODE_UNDER || v == DetectorNetworkNode.MODE_EQUAL || v == DetectorNetworkNode.MODE_ABOVE) {
            t.getNode().setMode(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Integer, DetectorTile> AMOUNT = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getAmount(), (t, v) -> {
        t.getNode().setAmount(v);
        t.getNode().markDirty();
    }, (initial, value) -> BaseScreen.executeLater(DetectorScreen.class, detectorScreen -> detectorScreen.updateAmountField(value)));

    private static final String NBT_POWERED = "Powered";

    public DetectorTile(BlockPos pos, BlockState state) {
        super(RSTiles.DETECTOR, pos, state);

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

package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DetectorTile extends NetworkNodeTile<DetectorNetworkNode> {
    public static final TileDataParameter<Integer, DetectorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, DetectorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, DetectorTile> MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getMode(), (t, v) -> {
        if (v == DetectorNetworkNode.MODE_UNDER || v == DetectorNetworkNode.MODE_EQUAL || v == DetectorNetworkNode.MODE_ABOVE) {
            t.getNode().setMode(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Integer, DetectorTile> AMOUNT = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getAmount(), (t, v) -> {
        t.getNode().setAmount(v);
        t.getNode().markDirty();
    });

    private static final String NBT_POWERED = "Powered";

    public DetectorTile() {
        super(RSTiles.DETECTOR);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        getNode().setPowered(tag.getBoolean(NBT_POWERED));

        super.readUpdate(tag);
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        tag.putBoolean(NBT_POWERED, getNode().isPowered());

        return tag;
    }

    @Override
    @Nonnull
    public DetectorNetworkNode createNode(World world, BlockPos pos) {
        return new DetectorNetworkNode(world, pos);
    }
}

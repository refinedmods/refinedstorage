package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.exporter.ExporterNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ExporterTile extends NetworkNodeTile<ExporterNetworkNode> {
    public static final TileDataParameter<Integer, ExporterTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ExporterTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, ExporterTile> CRAFT_ONLY = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isCraftOnly(), (t, v) -> {
        t.getNode().setCraftOnly(v);
        t.getNode().markDirty();
    });

    public ExporterTile() {
        super(RSTiles.EXPORTER);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(CRAFT_ONLY);
    }

    @Override
    @Nonnull
    public ExporterNetworkNode createNode(World world, BlockPos pos) {
        return new ExporterNetworkNode(world, pos);
    }
}

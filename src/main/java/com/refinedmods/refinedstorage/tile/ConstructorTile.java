package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ConstructorTile extends NetworkNodeTile<ConstructorNetworkNode> {
    public static final TileDataParameter<Integer, ConstructorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ConstructorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, ConstructorTile> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public ConstructorTile() {
        super(RSTiles.CONSTRUCTOR);
        
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    @Nonnull
    public ConstructorNetworkNode createNode(World world, BlockPos pos) {
        return new ConstructorNetworkNode(world, pos);
    }
}

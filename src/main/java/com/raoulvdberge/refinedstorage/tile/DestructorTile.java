package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.DestructorNetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DestructorTile extends NetworkNodeTile<DestructorNetworkNode> {
    public static final TileDataParameter<Integer, DestructorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, DestructorTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, DestructorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, DestructorTile> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public DestructorTile() {
        super(RSTiles.DESTRUCTOR);
        
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    @Nonnull
    public DestructorNetworkNode createNode(World world, BlockPos pos) {
        return new DestructorNetworkNode(world, pos);
    }
}

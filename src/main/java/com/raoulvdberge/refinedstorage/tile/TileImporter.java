package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeImporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileImporter extends NetworkNodeTile<NetworkNodeImporter> {
    public static final TileDataParameter<Integer, TileImporter> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileImporter> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TileImporter> TYPE = IType.createParameter();

    public TileImporter() {
        super(RSTiles.IMPORTER);
        
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    @Nonnull
    public NetworkNodeImporter createNode(World world, BlockPos pos) {
        return new NetworkNodeImporter(world, pos);
    }
}

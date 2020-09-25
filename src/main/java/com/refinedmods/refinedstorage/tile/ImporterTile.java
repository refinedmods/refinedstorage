package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ImporterTile extends NetworkNodeTile<ImporterNetworkNode> {
    public static final TileDataParameter<Integer, ImporterTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ImporterTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, ImporterTile> TYPE = IType.createParameter();

    public ImporterTile() {
        super(RSTiles.IMPORTER);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    @Nonnull
    public ImporterNetworkNode createNode(World world, BlockPos pos) {
        return new ImporterNetworkNode(world, pos);
    }
}

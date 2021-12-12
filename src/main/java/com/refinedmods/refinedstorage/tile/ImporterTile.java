package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class ImporterTile extends NetworkNodeTile<ImporterNetworkNode> {
    public static final TileDataParameter<Integer, ImporterTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ImporterTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, ImporterTile> TYPE = IType.createParameter();

    public static final TileDataParameter<CompoundNBT, ImporterTile> COVER_MANAGER = new TileDataParameter<>(DataSerializers.COMPOUND_TAG, new CompoundNBT(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> {});

    public ImporterTile() {
        super(RSTiles.IMPORTER);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public ImporterNetworkNode createNode(World world, BlockPos pos) {
        return new ImporterNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));

        requestModelDataUpdate();

        WorldUtils.updateBlock(level, worldPosition);
    }
}

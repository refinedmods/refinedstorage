package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class ExporterTile extends NetworkNodeTile<ExporterNetworkNode> {

    public static final TileDataParameter<CompoundTag, ExporterTile> COVER_MANAGER = new TileDataParameter<>(EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
        t -> t.getNode().getCoverManager().writeToNbt(),
        (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
        (initial, p) -> {
        });

    public static final TileDataParameter<Integer, ExporterTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ExporterTile> TYPE = IType.createParameter();

    public ExporterTile(BlockPos pos, BlockState state) {
        super(RSTiles.EXPORTER, pos, state);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public ExporterNetworkNode createNode(Level level, BlockPos pos) {
        return new ExporterNetworkNode(level, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));

        requestModelDataUpdate();

        WorldUtils.updateBlock(level, worldPosition);
    }
}

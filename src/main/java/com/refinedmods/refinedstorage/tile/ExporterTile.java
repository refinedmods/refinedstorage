package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
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

public class ExporterTile extends NetworkNodeTile<ExporterNetworkNode> {

    public static final TileDataParameter<CompoundNBT, ExporterTile> COVER_MANAGER = new TileDataParameter<>(DataSerializers.COMPOUND_NBT, new CompoundNBT(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> Minecraft.getInstance().enqueue(() -> {}));

    public static final TileDataParameter<Integer, ExporterTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ExporterTile> TYPE = IType.createParameter();

    static {
        TileDataManager.registerParameter(COVER_MANAGER);
    }

    public ExporterTile() {
        super(RSTiles.EXPORTER);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public ExporterNetworkNode createNode(World world, BlockPos pos) {
        return new ExporterNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);
        tag.put("Covers", this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound("Covers"));

        requestModelDataUpdate();

        WorldUtils.updateBlock(world, pos);
    }
}

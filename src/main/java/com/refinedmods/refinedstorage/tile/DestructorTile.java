package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode;
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

public class DestructorTile extends NetworkNodeTile<DestructorNetworkNode> {
    public static final TileDataParameter<Integer, DestructorTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, DestructorTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, DestructorTile> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, DestructorTile> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public static final TileDataParameter<CompoundNBT, DestructorTile> COVER_MANAGER = new TileDataParameter<>(DataSerializers.COMPOUND_TAG, new CompoundNBT(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> {});

    public DestructorTile() {
        super(RSTiles.DESTRUCTOR);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public DestructorNetworkNode createNode(World world, BlockPos pos) {
        return new DestructorNetworkNode(world, pos);
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

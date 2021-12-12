package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.PortableGridBlock;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGrid;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PortableGridBakedModel extends DelegateBakedModel {
    private final IBakedModel baseConnected;
    private final IBakedModel baseDisconnected;
    private final IBakedModel disk;
    private final IBakedModel diskNearCapacity;
    private final IBakedModel diskFull;
    private final IBakedModel diskDisconnected;

    private final CustomItemOverrideList itemOverrideList = new CustomItemOverrideList();

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(@Nonnull CacheKey key) {
            Direction direction = key.state.getValue(RSBlocks.PORTABLE_GRID.get().getDirection().getProperty());
            boolean active = key.state.getValue(PortableGridBlock.ACTIVE);
            PortableGridDiskState diskState = key.state.getValue(PortableGridBlock.DISK_STATE);

            List<BakedQuad> quads = new ArrayList<>(QuadTransformer.getTransformedQuads(
                active ? baseConnected : baseDisconnected,
                direction,
                null,
                key.state,
                key.random,
                key.side
            ));

            IBakedModel diskModel = getDiskModel(diskState);
            if (diskModel != null) {
                quads.addAll(QuadTransformer.getTransformedQuads(diskModel, direction, null, key.state, key.random, key.side));
            }

            return quads;
        }
    });

    public PortableGridBakedModel(IBakedModel baseConnected,
                                  IBakedModel baseDisconnected,
                                  IBakedModel disk,
                                  IBakedModel diskNearCapacity,
                                  IBakedModel diskFull,
                                  IBakedModel diskDisconnected) {
        super(baseConnected);

        this.baseConnected = baseConnected;
        this.baseDisconnected = baseDisconnected;
        this.disk = disk;
        this.diskNearCapacity = diskNearCapacity;
        this.diskFull = diskFull;
        this.diskDisconnected = diskDisconnected;
    }

    @Nullable
    private IBakedModel getDiskModel(PortableGridDiskState state) {
        switch (state) {
            case NORMAL:
                return disk;
            case NEAR_CAPACITY:
                return diskNearCapacity;
            case FULL:
                return diskFull;
            case DISCONNECTED:
                return diskDisconnected;
            case NONE:
                return null;
            default:
                return null;
        }
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverrideList;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state != null) {
            return cache.getUnchecked(new CacheKey(state, side, rand));
        }

        return super.getQuads(state, side, rand);
    }

    private class CustomItemOverrideList extends ItemOverrideList {
        @Nullable
        @Override
        public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            PortableGrid portableGrid = new PortableGrid(null, stack, new PlayerSlot(-1));

            if (portableGrid.isGridActive()) {
                return new PortableGridItemBakedModel(baseConnected, getDiskModel(portableGrid.getDiskState()));
            } else {
                return new PortableGridItemBakedModel(baseDisconnected, getDiskModel(portableGrid.getDiskState()));
            }
        }
    }

    private static class CacheKey {
        private final BlockState state;
        private final Direction side;
        private final Random random;

        public CacheKey(BlockState state, Direction side, Random random) {
            this.state = state;
            this.side = side;
            this.random = random;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return state.equals(cacheKey.state) &&
                side == cacheKey.side &&
                random.equals(cacheKey.random);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, side, random);
        }
    }
}

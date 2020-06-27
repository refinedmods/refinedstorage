package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.PortableGridBlock;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGrid;
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

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
            List<BakedQuad> quads = new ArrayList<>();

            if (key.active) {
                quads.addAll(new TRSRBakedModel(baseConnected, key.direction).getQuads(key.state, null, key.random));
            } else {
                quads.addAll(new TRSRBakedModel(baseDisconnected, key.direction).getQuads(key.state, null, key.random));
            }

            switch (key.diskState) {
                case NORMAL:
                    quads.addAll(new TRSRBakedModel(disk, key.direction).getQuads(key.state, null, key.random));
                    break;
                case NEAR_CAPACITY:
                    quads.addAll(new TRSRBakedModel(diskNearCapacity, key.direction).getQuads(key.state, null, key.random));
                    break;
                case FULL:
                    quads.addAll(new TRSRBakedModel(diskFull, key.direction).getQuads(key.state, null, key.random));
                    break;
                case DISCONNECTED:
                    quads.addAll(new TRSRBakedModel(diskDisconnected, key.direction).getQuads(key.state, null, key.random));
                    break;
                case NONE:
                    break;
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

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverrideList;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (state != null) {
            Direction direction = state.get(RSBlocks.PORTABLE_GRID.getDirection().getProperty());
            boolean active = state.get(PortableGridBlock.ACTIVE);
            PortableGridDiskState diskState = state.get(PortableGridBlock.DISK_STATE);

            return cache.getUnchecked(new CacheKey(direction, diskState, active, rand, state));
        }

        return super.getQuads(state, side, rand);
    }

    private class CustomItemOverrideList extends ItemOverrideList {
        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
            PortableGrid portableGrid = new PortableGrid(null, stack, -1);

            IBakedModel myDisk = null;

            switch (portableGrid.getDiskState()) {
                case NORMAL:
                    myDisk = disk;
                    break;
                case NEAR_CAPACITY:
                    myDisk = diskNearCapacity;
                    break;
                case FULL:
                    myDisk = diskFull;
                    break;
                case DISCONNECTED:
                    myDisk = diskDisconnected;
                    break;
                case NONE:
                    break;
            }

            if (portableGrid.isGridActive()) {
                return new PortableGridItemBakedModel(baseConnected, myDisk);
            } else {
                return new PortableGridItemBakedModel(baseDisconnected, myDisk);
            }
        }
    }

    private static class CacheKey {
        private final Direction direction;
        private final PortableGridDiskState diskState;
        private final boolean active;
        private final Random random;
        private final BlockState state;

        public CacheKey(Direction direction, PortableGridDiskState diskState, boolean active, Random random, BlockState state) {
            this.direction = direction;
            this.diskState = diskState;
            this.active = active;
            this.random = random;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return active == cacheKey.active &&
                direction == cacheKey.direction &&
                diskState == cacheKey.diskState;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, diskState, active);
        }
    }
}

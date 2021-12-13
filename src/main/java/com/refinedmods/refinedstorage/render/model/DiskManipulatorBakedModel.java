package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.math.Vector3f;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiskManipulatorBakedModel extends DelegateBakedModel {
    private final BakedModel baseConnected;
    private final BakedModel baseDisconnected;
    private final BakedModel disk;
    private final BakedModel diskNearCapacity;
    private final BakedModel diskFull;
    private final BakedModel diskDisconnected;
    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.getValue(RSBlocks.DISK_MANIPULATOR.get(ColorMap.DEFAULT_COLOR).get().getDirection().getProperty());
            boolean connected = key.state.getValue(NetworkNodeBlock.CONNECTED);

            List<BakedQuad> quads = new ArrayList<>(QuadTransformer.getTransformedQuads(
                connected ? baseConnected : baseDisconnected,
                facing,
                null,
                key.state,
                key.random,
                key.side
            ));

            int x = 0;
            int y = 0;
            for (int i = 0; i < 6; ++i) {
                if (key.diskState[i] != DiskState.NONE) {
                    BakedModel diskModel = getDiskModel(key.diskState[i]);

                    quads.addAll(QuadTransformer.getTransformedQuads(
                        diskModel,
                        facing,
                        getDiskTranslation(facing, x, y),
                        key.state,
                        key.random,
                        key.side
                    ));
                }

                y++;
                if ((i + 1) % 3 == 0) {
                    x++;
                    y = 0;
                }
            }

            return quads;
        }

        private BakedModel getDiskModel(DiskState diskState) {
            switch (diskState) {
                case DISCONNECTED:
                    return diskDisconnected;
                case NEAR_CAPACITY:
                    return diskNearCapacity;
                case FULL:
                    return diskFull;
                default:
                    return disk;
            }
        }

        private Vector3f getDiskTranslation(Direction facing, int x, int y) {
            Vector3f translation = new Vector3f();

            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                translation.add((2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.NORTH ? -1 : 1), 0, 0); // Add to X
            } else if (facing == Direction.EAST || facing == Direction.WEST) {
                translation.add(0, 0, (2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.EAST ? -1 : 1)); // Add to Z
            }

            translation.add(0, -((6F / 16F) + (3F * y) / 16F), 0); // Remove from Y

            return translation;
        }
    });

    public DiskManipulatorBakedModel(BakedModel baseConnected, BakedModel baseDisconnected, BakedModel disk, BakedModel diskNearCapacity, BakedModel diskFull, BakedModel diskDisconnected) {
        super(baseConnected);

        this.baseConnected = baseConnected;
        this.baseDisconnected = baseDisconnected;
        this.disk = disk;
        this.diskNearCapacity = diskNearCapacity;
        this.diskFull = diskFull;
        this.diskDisconnected = diskDisconnected;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        DiskState[] diskState = data.getData(DiskManipulatorBlockEntity.DISK_STATE_PROPERTY);

        if (diskState == null) {
            return base.getQuads(state, side, rand, data);
        }

        CacheKey key = new CacheKey(state, side, diskState, rand);

        return cache.getUnchecked(key);
    }

    private static class CacheKey {
        private final BlockState state;
        private final Direction side;
        private final DiskState[] diskState;
        private final Random random;

        CacheKey(BlockState state, @Nullable Direction side, DiskState[] diskState, Random random) {
            this.state = state;
            this.side = side;
            this.diskState = diskState;
            this.random = random;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            if (!state.equals(cacheKey.state)) {
                return false;
            }

            if (side != cacheKey.side) {
                return false;
            }

            return Arrays.equals(diskState, cacheKey.diskState);
        }

        @Override
        public int hashCode() {
            int result = state.hashCode();
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(diskState);
            return result;
        }
    }
}

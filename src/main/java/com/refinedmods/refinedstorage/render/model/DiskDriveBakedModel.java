package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.tile.DiskDriveTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DiskDriveBakedModel extends DelegateBakedModel {
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

    private final Map<Direction, IBakedModel> baseByFacing = new HashMap<>();
    private final Map<Direction, Map<DiskState, List<IBakedModel>>> disksByFacing = new HashMap<>();

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.get(RSBlocks.DISK_DRIVE.getDirection().getProperty());

            List<BakedQuad> quads = new ArrayList<>(baseByFacing.get(facing).getQuads(key.state, key.side, key.random));

            for (int i = 0; i < 8; ++i) {
                if (key.diskState[i] != DiskState.NONE) {
                    quads.addAll(disksByFacing.get(facing).get(key.diskState[i]).get(i).getQuads(key.state, key.side, key.random));
                }
            }

            return quads;
        }
    });

    public DiskDriveBakedModel(IBakedModel base,
                               IBakedModel disk,
                               IBakedModel diskNearCapacity,
                               IBakedModel diskFull,
                               IBakedModel diskDisconnected) {
        super(base);

        for (Direction facing : Direction.values()) {
            if (facing.getHorizontalIndex() == -1) {
                continue;
            }

            baseByFacing.put(facing, new TRSRBakedModel(base, facing, null));
            disksByFacing.put(facing, new HashMap<>());

            addDiskModels(disk, DiskState.NORMAL, facing);
            addDiskModels(diskNearCapacity, DiskState.NEAR_CAPACITY, facing);
            addDiskModels(diskFull, DiskState.FULL, facing);
            addDiskModels(diskDisconnected, DiskState.DISCONNECTED, facing);
        }
    }

    private void addDiskModels(IBakedModel disk, DiskState type, Direction facing) {
        disksByFacing.get(facing).put(type, new ArrayList<>());

        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 2; ++x) {
                Vector3f trans = new Vector3f();

                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    trans.add(((2F / 16F) + ((float) x * 7F) / 16F) * (facing == Direction.NORTH ? -1 : 1), 0, 0); // Add to X
                } else if (facing == Direction.EAST || facing == Direction.WEST) {
                    trans.add(0, 0, ((2F / 16F) + ((float) x * 7F) / 16F) * (facing == Direction.EAST ? -1 : 1)); // Add to Y
                }

                trans.add(0, -((2F / 16F) + ((float) y * 3F) / 16F), 0); // Remove from Y

                disksByFacing.get(facing).get(type).add(new TRSRBakedModel(disk, facing, trans));
            }
        }
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        DiskState[] diskState = data.getData(DiskDriveTile.DISK_STATE_PROPERTY);

        if (diskState == null) {
            return base.getQuads(state, side, rand, data);
        }

        CacheKey key = new CacheKey(state, side, diskState, rand);

        return cache.getUnchecked(key);
    }
}

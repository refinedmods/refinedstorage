package com.raoulvdberge.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode;
import com.raoulvdberge.refinedstorage.tile.DiskDriveTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.*;

public class DiskDriveBakedModel extends DelegateBakedModel {
    private class CacheKey {
        private BlockState state;
        private Direction side;
        private DiskDriveNetworkNode.DiskState[] diskState;
        private Random random;

        CacheKey(BlockState state, @Nullable Direction side, DiskDriveNetworkNode.DiskState[] diskState, Random random) {
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

    private Map<Direction, IBakedModel> baseByFacing = new HashMap<>();
    private Map<Direction, Map<DiskDriveNetworkNode.DiskState, List<IBakedModel>>> disksByFacing = new HashMap<>();

    private LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.get(RSBlocks.DISK_DRIVE.getDirection().getProperty());

            List<BakedQuad> quads = new ArrayList<>(baseByFacing.get(facing).getQuads(key.state, key.side, key.random));

            for (int i = 0; i < 8; ++i) {
                if (key.diskState[i] != DiskDriveNetworkNode.DiskState.NONE) {
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

            baseByFacing.put(facing, new TRSRBakedModel(base, facing));

            disksByFacing.put(facing, new HashMap<>());

            addDiskModels(disk, DiskDriveNetworkNode.DiskState.NORMAL, facing);
            addDiskModels(diskNearCapacity, DiskDriveNetworkNode.DiskState.NEAR_CAPACITY, facing);
            addDiskModels(diskFull, DiskDriveNetworkNode.DiskState.FULL, facing);
            addDiskModels(diskDisconnected, DiskDriveNetworkNode.DiskState.DISCONNECTED, facing);
        }
    }

    private void addDiskModels(IBakedModel disk, DiskDriveNetworkNode.DiskState type, Direction facing) {
        disksByFacing.get(facing).put(type, new ArrayList<>());

        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 2; ++x) {
                TRSRBakedModel model = new TRSRBakedModel(disk, facing);

                Vector3f trans = model.transformation.getTranslation();

                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    trans.x += ((2F / 16F) + ((float) x * 7F) / 16F) * (facing == Direction.NORTH ? -1 : 1);
                } else if (facing == Direction.EAST || facing == Direction.WEST) {
                    trans.z += ((2F / 16F) + ((float) x * 7F) / 16F) * (facing == Direction.EAST ? -1 : 1);
                }

                trans.y -= (2F / 16F) + ((float) y * 3F) / 16F;

                model.transformation = new TRSRTransformation(trans, model.transformation.getLeftRot(), model.transformation.getScale(), model.transformation.getRightRot());

                disksByFacing.get(facing).get(type).add(model);
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data) {
        DiskDriveNetworkNode.DiskState[] diskState = data.getData(DiskDriveTile.DISK_STATE_PROPERTY);

        if (diskState == null) {
            return base.getQuads(state, side, rand, data);
        }

        CacheKey key = new CacheKey(state, side, diskState, rand);

        return cache.getUnchecked(key);
    }
}

package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.block.DiskManipulatorBlock;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DiskManipulatorBakedModel extends DelegateBakedModel {
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

    private final Map<Direction, IBakedModel> modelsConnected = new HashMap<>();
    private final Map<Direction, IBakedModel> modelsDisconnected = new HashMap<>();
    private final Map<Direction, Map<DiskState, List<IBakedModel>>> disks = new HashMap<>();

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.get(RSBlocks.DISK_MANIPULATOR.getDirection().getProperty());

            List<BakedQuad> quads = new ArrayList<>((key.state.get(DiskManipulatorBlock.CONNECTED) ? modelsConnected : modelsDisconnected).get(facing).getQuads(key.state, key.side, key.random));

            for (int i = 0; i < 6; ++i) {
                if (key.diskState[i] != DiskState.NONE) {
                    quads.addAll(disks.get(facing).get(key.diskState[i]).get(i).getQuads(key.state, key.side, key.random));
                }
            }

            return quads;
        }
    });

    public DiskManipulatorBakedModel(IBakedModel baseConnected,
                                     IBakedModel baseDisconnected,
                                     IBakedModel disk,
                                     IBakedModel diskNearCapacity,
                                     IBakedModel diskFull,
                                     IBakedModel diskDisconnected) {
        super(baseDisconnected);

        for (Direction facing : Direction.values()) {
            if (facing.getHorizontalIndex() == -1) {
                continue;
            }

            modelsConnected.put(facing, new TRSRBakedModel(baseConnected, facing));
            modelsDisconnected.put(facing, new TRSRBakedModel(baseDisconnected, facing));

            disks.put(facing, new HashMap<>());

            disks.get(facing).put(DiskState.NORMAL, new ArrayList<>());
            disks.get(facing).put(DiskState.NEAR_CAPACITY, new ArrayList<>());
            disks.get(facing).put(DiskState.FULL, new ArrayList<>());
            disks.get(facing).put(DiskState.DISCONNECTED, new ArrayList<>());

            addDiskModels(disk, DiskState.NORMAL, facing);
            addDiskModels(diskNearCapacity, DiskState.NEAR_CAPACITY, facing);
            addDiskModels(diskFull, DiskState.FULL, facing);
            addDiskModels(diskDisconnected, DiskState.DISCONNECTED, facing);
        }
    }

    private void addDiskModels(IBakedModel disk, DiskState type, Direction facing) {
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 3; ++y) {
                TRSRBakedModel model = new TRSRBakedModel(disk, facing);

                Vector3f trans = model.transformation.getTranslation();

                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    trans.add((2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.NORTH ? -1 : 1), 0, 0); // Add to X
                } else if (facing == Direction.EAST || facing == Direction.WEST) {
                    trans.add(0, 0, (2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.EAST ? -1 : 1)); // Add to Z
                }

                trans.add(0, -((6F / 16F) + (3F * y) / 16F), 0); // Remove from Y
                
                model.transformation = new TransformationMatrix(trans, model.transformation.getRotationLeft(), model.transformation.getScale(), model.transformation.getRightRot());

                disks.get(facing).get(type).add(model);
            }
        }
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        DiskState[] diskState = data.getData(DiskManipulatorTile.DISK_STATE_PROPERTY);

        if (diskState == null) {
            return base.getQuads(state, side, rand, data);
        }

        CacheKey key = new CacheKey(state, side, diskState, rand);

        return cache.getUnchecked(key);
    }
}

package com.raoulvdberge.refinedstorage.render.model.baked;
/*
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.block.BlockDiskManipulator;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsDisk;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.*;

public class BakedModelDiskManipulator extends BakedModelDelegate {
    private class CacheKey {
        private BlockState state;
        private Direction side;
        private Integer[] diskState;

        CacheKey(BlockState state, @Nullable Direction side, Integer[] diskState) {
            this.state = state;
            this.side = side;
            this.diskState = diskState;
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

    private Map<Direction, IBakedModel> modelsConnected = new HashMap<>();
    private Map<Direction, IBakedModel> modelsDisconnected = new HashMap<>();
    private Map<Direction, Map<Integer, List<IBakedModel>>> disks = new HashMap<>();

    private LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.getValue(RSBlocks.DISK_MANIPULATOR.getDirection().getProperty());

            List<BakedQuad> quads = (key.state.getValue(BlockDiskManipulator.CONNECTED) ? modelsConnected : modelsDisconnected).get(facing).getQuads(key.state, key.side, 0);

            for (int i = 0; i < 6; ++i) {
                if (key.diskState[i] != ConstantsDisk.DISK_STATE_NONE) {
                    quads.addAll(disks.get(facing).get(key.diskState[i]).get(i).getQuads(key.state, key.side, 0));
                }
            }

            return quads;
        }
    });

    public BakedModelDiskManipulator(IBakedModel baseConnected, IBakedModel baseDisconnected, IBakedModel disk, IBakedModel diskNearCapacity, IBakedModel diskFull, IBakedModel diskDisconnected) {
        super(baseDisconnected);

        for (Direction facing : Direction.HORIZONTALS) {
            modelsConnected.put(facing, new BakedModelTRSR(baseConnected, facing));
            modelsDisconnected.put(facing, new BakedModelTRSR(baseDisconnected, facing));

            disks.put(facing, new HashMap<>());

            disks.get(facing).put(ConstantsDisk.DISK_STATE_NORMAL, new ArrayList<>());
            disks.get(facing).put(ConstantsDisk.DISK_STATE_NEAR_CAPACITY, new ArrayList<>());
            disks.get(facing).put(ConstantsDisk.DISK_STATE_FULL, new ArrayList<>());
            disks.get(facing).put(ConstantsDisk.DISK_STATE_DISCONNECTED, new ArrayList<>());

            initDiskModels(disk, ConstantsDisk.DISK_STATE_NORMAL, facing);
            initDiskModels(diskNearCapacity, ConstantsDisk.DISK_STATE_NEAR_CAPACITY, facing);
            initDiskModels(diskFull, ConstantsDisk.DISK_STATE_FULL, facing);
            initDiskModels(diskDisconnected, ConstantsDisk.DISK_STATE_DISCONNECTED, facing);
        }
    }

    private void initDiskModels(IBakedModel disk, int type, Direction facing) {
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 3; ++y) {
                BakedModelTRSR model = new BakedModelTRSR(disk, facing);

                Vector3f trans = model.transformation.getTranslation();

                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    trans.x += (2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.NORTH ? -1 : 1);
                } else if (facing == Direction.EAST || facing == Direction.WEST) {
                    trans.z += (2F / 16F + ((float) x * 7F) / 16F) * (facing == Direction.EAST ? -1 : 1);
                }

                trans.y -= (6F / 16F) + (3F * y) / 16F;

                model.transformation = new TRSRTransformation(trans, model.transformation.getLeftRot(), model.transformation.getScale(), model.transformation.getRightRot());

                disks.get(facing).get(type).add(model);
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand) {
        if (!(state instanceof IExtendedBlockState)) {
            return base.getQuads(state, side, rand);
        }

        Integer[] diskState = ((IExtendedBlockState) state).getValue(BlockDiskManipulator.DISK_STATE);

        if (diskState == null) {
            return base.getQuads(state, side, rand);
        }

        CacheKey key = new CacheKey(((IExtendedBlockState) state).getClean(), side, diskState);

        return cache.getUnchecked(key);
    }
}
*/
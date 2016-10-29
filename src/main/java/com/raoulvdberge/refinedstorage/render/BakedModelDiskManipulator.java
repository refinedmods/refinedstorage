package com.raoulvdberge.refinedstorage.render;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.BlockDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.*;

public class BakedModelDiskManipulator implements IBakedModel {
    private class CacheKey {
        private IBlockState state;
        private EnumFacing side;
        private Integer[] diskState;

        CacheKey(IBlockState state, @Nullable EnumFacing side, Integer[] diskState) {
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

    private IBakedModel baseConnected;
    private IBakedModel baseDisconnected;
    private Map<EnumFacing, IBakedModel> modelsConnected = new HashMap<>();
    private Map<EnumFacing, IBakedModel> modelsDisconnected = new HashMap<>();
    private Map<EnumFacing, Map<Integer, List<IBakedModel>>> disks = new HashMap<>();

    private LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) throws Exception {
            EnumFacing facing = key.state.getValue(BlockBase.DIRECTION);

            List<BakedQuad> quads = (key.state.getValue(BlockDiskManipulator.CONNECTED) ? modelsConnected : modelsDisconnected).get(facing).getQuads(key.state, key.side, 0);

            for (int i = 0; i < 6; ++i) {
                if (key.diskState[i] != TileDiskDrive.DISK_STATE_NONE) {
                    quads.addAll(disks.get(facing).get(key.diskState[i]).get(i).getQuads(key.state, key.side, 0));
                }
            }

            return quads;
        }
    });

    public BakedModelDiskManipulator(IBakedModel baseConnected, IBakedModel baseDisconnected, IBakedModel disk, IBakedModel diskFull, IBakedModel diskDisconnected) {
        this.baseConnected = baseConnected;
        this.baseDisconnected = baseDisconnected;

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            modelsConnected.put(facing, new BakedModelTRSR(baseConnected, facing));
            modelsDisconnected.put(facing, new BakedModelTRSR(baseDisconnected, facing));

            disks.put(facing, new HashMap<>());

            disks.get(facing).put(TileDiskDrive.DISK_STATE_NORMAL, new ArrayList<>());
            disks.get(facing).put(TileDiskDrive.DISK_STATE_FULL, new ArrayList<>());
            disks.get(facing).put(TileDiskDrive.DISK_STATE_DISCONNECTED, new ArrayList<>());

            initDiskModels(disk, TileDiskDrive.DISK_STATE_NORMAL, facing);
            initDiskModels(diskFull, TileDiskDrive.DISK_STATE_FULL, facing);
            initDiskModels(diskDisconnected, TileDiskDrive.DISK_STATE_DISCONNECTED, facing);
        }
    }

    private void initDiskModels(IBakedModel disk, int type, EnumFacing facing) {
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 3; ++y) {
                BakedModelTRSR model = new BakedModelTRSR(disk, facing);

                Vector3f trans = model.transformation.getTranslation();

                if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                    trans.x += (2F / 16F + ((float) x * 7F) / 16F) * (facing == EnumFacing.NORTH ? -1 : 1);
                } else if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
                    trans.z += (2F / 16F + ((float) x * 7F) / 16F) * (facing == EnumFacing.EAST ? -1 : 1);
                }

                trans.y -= (6F / 16F) + (3F * y) / 16F;

                model.transformation = new TRSRTransformation(trans, model.transformation.getLeftRot(), model.transformation.getScale(), model.transformation.getRightRot());

                disks.get(facing).get(type).add(model);
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (!(state instanceof IExtendedBlockState)) {
            return baseDisconnected.getQuads(state, side, rand);
        }

        Integer[] diskState = ((IExtendedBlockState) state).getValue(BlockDiskManipulator.DISK_STATE);

        if (diskState == null) {
            return baseDisconnected.getQuads(state, side, rand);
        }

        CacheKey key = new CacheKey(((IExtendedBlockState) state).getClean(), side, diskState);
        cache.refresh(key);
        return cache.getUnchecked(key);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseDisconnected.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseDisconnected.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return baseDisconnected.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseDisconnected.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseDisconnected.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return baseDisconnected.getOverrides();
    }
}

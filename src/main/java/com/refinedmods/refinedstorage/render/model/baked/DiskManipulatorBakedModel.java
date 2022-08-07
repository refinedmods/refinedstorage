package com.refinedmods.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.math.Vector3f;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskManipulatorBakedModel extends BakedModelWrapper<BakedModel> {
    private final Function<Direction, BakedModel> baseConnectedModelBakery;
    private final Function<Direction, BakedModel> baseDisconnectedModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery;
    private final BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery;

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(CacheKey key) {
            Direction facing = key.state.getValue(RSBlocks.DISK_MANIPULATOR.get(ColorMap.DEFAULT_COLOR).get().getDirection().getProperty());
            boolean connected = key.state.getValue(NetworkNodeBlock.CONNECTED);

            List<BakedQuad> quads = new ArrayList<>((connected ? baseConnectedModelBakery : baseDisconnectedModelBakery)
                .apply(facing).getQuads(key.state, key.side, key.random));

            int x = 0;
            int y = 0;
            for (int i = 0; i < 6; ++i) {
                if (key.diskState[i] != DiskState.NONE) {
                    BakedModel diskModel = getDiskModelBakery(key.diskState[i]).apply(facing, getDiskTranslation(facing, x, y));
                    quads.addAll(diskModel.getQuads(key.state, key.side, key.random));
                }

                y++;
                if ((i + 1) % 3 == 0) {
                    x++;
                    y = 0;
                }
            }

            return quads;
        }

        private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBakery(DiskState diskState) {
            return switch (diskState) {
                case DISCONNECTED -> diskDisconnectedModelBakery;
                case NEAR_CAPACITY -> diskNearCapacityModelBakery;
                case FULL -> diskFullModelBakery;
                default -> diskModelBakery;
            };
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

    public DiskManipulatorBakedModel(BakedModel originalModel, Function<Direction, BakedModel> baseConnectedModelBakery, Function<Direction, BakedModel> baseDisconnectedModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskNearCapacityModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskFullModelBakery, BiFunction<Direction, Vector3f, BakedModel> diskDisconnectedModelBakery) {
        super(originalModel);
        this.baseConnectedModelBakery = baseConnectedModelBakery;
        this.baseDisconnectedModelBakery = baseDisconnectedModelBakery;
        this.diskModelBakery = diskModelBakery;
        this.diskNearCapacityModelBakery = diskNearCapacityModelBakery;
        this.diskFullModelBakery = diskFullModelBakery;
        this.diskDisconnectedModelBakery = diskDisconnectedModelBakery;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable final BlockState state,
                                    @Nullable final Direction side,
                                    @Nonnull final RandomSource rand,
                                    @Nonnull final ModelData extraData,
                                    @Nullable final RenderType renderType) {
        DiskState[] diskState = extraData.get(DiskManipulatorBlockEntity.DISK_STATE_PROPERTY);

        if (diskState == null) {
            return super.getQuads(state, side, rand, extraData, renderType);
        }

        CacheKey key = new CacheKey(state, side, diskState, rand);

        return cache.getUnchecked(key);
    }

    private static class CacheKey {
        private final BlockState state;
        private final Direction side;
        private final DiskState[] diskState;
        private final RandomSource random;

        CacheKey(BlockState state, @Nullable Direction side, DiskState[] diskState, RandomSource random) {
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

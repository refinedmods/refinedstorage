package com.refinedmods.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.PortableGridBlock;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGrid;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridDiskState;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PortableGridBakedModel extends BakedModelWrapper<BakedModel> {
    private final Function<Direction, BakedModel> baseConnectedModelBakery;
    private final Function<Direction, BakedModel> baseDisconnectedModelBakery;
    private final Function<Direction, BakedModel> diskModelBakery;
    private final Function<Direction, BakedModel> diskNearCapacityModelBakery;
    private final Function<Direction, BakedModel> diskFullModelBakery;
    private final Function<Direction, BakedModel> diskDisconnectedModelBakery;

    private final RenderTypeGroup renderTypes;

    private final CustomItemOverrideList itemOverrideList = new CustomItemOverrideList();

    private final LoadingCache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        @SuppressWarnings("deprecation")
        public List<BakedQuad> load(@Nonnull CacheKey key) {
            Direction direction = key.state.getValue(RSBlocks.PORTABLE_GRID.get().getDirection().getProperty());
            boolean active = key.state.getValue(PortableGridBlock.ACTIVE);
            PortableGridDiskState diskState = key.state.getValue(PortableGridBlock.DISK_STATE);

            List<BakedQuad> quads = new ArrayList<>((active ? baseConnectedModelBakery : baseDisconnectedModelBakery)
                .apply(direction).getQuads(key.state, key.side, key.random));

            Function<Direction, BakedModel> diskModel = getDiskModelBakery(diskState);
            if (diskModel != null) {
                quads.addAll(diskModel.apply(direction).getQuads(key.state, key.side, key.random));
            }

            return quads;
        }
    });

    public PortableGridBakedModel(BakedModel baseModel, Function<Direction, BakedModel> baseConnectedModelBakery, Function<Direction, BakedModel> baseDisconnectedModelBakery, Function<Direction, BakedModel> diskModelBakery, Function<Direction, BakedModel> diskNearCapacityModelBakery, Function<Direction, BakedModel> diskFullModelBakery, Function<Direction, BakedModel> diskDisconnectedModelBakery, RenderTypeGroup renderTypes) {
        super(baseModel);
        this.baseConnectedModelBakery = baseConnectedModelBakery;
        this.baseDisconnectedModelBakery = baseDisconnectedModelBakery;
        this.diskModelBakery = diskModelBakery;
        this.diskNearCapacityModelBakery = diskNearCapacityModelBakery;
        this.diskFullModelBakery = diskFullModelBakery;
        this.diskDisconnectedModelBakery = diskDisconnectedModelBakery;
        this.renderTypes = renderTypes;
    }

    @Nullable
    private Function<Direction, BakedModel> getDiskModelBakery(PortableGridDiskState state) {
        return switch (state) {
            case NORMAL -> diskModelBakery;
            case NEAR_CAPACITY -> diskNearCapacityModelBakery;
            case FULL -> diskFullModelBakery;
            case DISCONNECTED -> diskDisconnectedModelBakery;
            case NONE -> null;
        };
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(renderTypes.block());
    }

    @Override
    public ItemOverrides getOverrides() {
        return itemOverrideList;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (state != null) {
            return cache.getUnchecked(new CacheKey(state, side, rand));
        }
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    private static class CacheKey {
        private final BlockState state;
        private final Direction side;
        private final RandomSource random;

        public CacheKey(BlockState state, Direction side, RandomSource random) {
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

    private class CustomItemOverrideList extends ItemOverrides {
        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
            PortableGrid portableGrid = new PortableGrid(null, stack, new PlayerSlot(-1));

            Function<Direction, BakedModel> diskModelBakery = getDiskModelBakery(portableGrid.getDiskState());
            BakedModel diskModel = diskModelBakery == null ? null : diskModelBakery.apply(Direction.NORTH);

            if (portableGrid.isGridActive()) {
                return new PortableGridItemBakedModel(
                    baseConnectedModelBakery.apply(Direction.NORTH),
                    diskModel
                );
            } else {
                return new PortableGridItemBakedModel(
                    baseDisconnectedModelBakery.apply(Direction.NORTH),
                    diskModel
                );
            }
        }
    }
}

package com.refinedmods.refinedstorage.render.model.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortableGridItemBakedModel extends BakedModelWrapper<BakedModel> {
    @Nullable
    private final BakedModel disk;

    public PortableGridItemBakedModel(BakedModel base, @Nullable BakedModel disk) {
        super(base);
        this.disk = disk;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
        List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand));

        if (disk != null) {
            quads.addAll(disk.getQuads(state, side, rand));
        }

        return quads;
    }
}

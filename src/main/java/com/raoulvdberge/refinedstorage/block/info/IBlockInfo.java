package com.raoulvdberge.refinedstorage.block.info;

import com.raoulvdberge.refinedstorage.tile.BaseTile;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IBlockInfo {
    Material getMaterial();

    SoundType getSoundType();

    ResourceLocation getId();

    Object getModObject();

    float getHardness();

    @Nullable
    BaseTile createTileEntity();

    boolean hasTileEntity();
}

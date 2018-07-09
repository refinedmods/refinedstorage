package com.raoulvdberge.refinedstorage.block.info;

import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import javax.annotation.Nullable;

public interface IBlockInfo {
    Material getMaterial();

    SoundType getSoundType();

    String getId();

    String getModId();

    Object getModObject();

    float getHardness();

    @Nullable
    TileBase createTileEntity();

    boolean hasTileEntity();
}

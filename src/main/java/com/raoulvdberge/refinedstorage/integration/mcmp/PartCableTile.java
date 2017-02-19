package com.raoulvdberge.refinedstorage.integration.mcmp;

import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.tileentity.TileEntity;

public class PartCableTile implements IMultipartTile {
    private TileEntity tile;

    public PartCableTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }
}

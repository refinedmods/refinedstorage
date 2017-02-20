package com.raoulvdberge.refinedstorage.integration.mcmp;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.tileentity.TileEntity;

public class PartCableTile implements IMultipartTile {
    private TileEntity tile;
    private IPartInfo info;

    public PartCableTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void setPartInfo(IPartInfo info) {
        this.info = info;
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }

    public IPartInfo getInfo() {
        return info;
    }
}

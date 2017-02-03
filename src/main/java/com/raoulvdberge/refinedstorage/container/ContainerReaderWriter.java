package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerReaderWriter extends ContainerBase {
    private IGuiReaderWriter readerWriter;

    public ContainerReaderWriter(IGuiReaderWriter readerWriter, TileBase tile, EntityPlayer player) {
        super(tile, player);

        this.readerWriter = readerWriter;

        addPlayerInventory(8, 127);
    }

    public IGuiReaderWriter getReaderWriter() {
        return readerWriter;
    }
}

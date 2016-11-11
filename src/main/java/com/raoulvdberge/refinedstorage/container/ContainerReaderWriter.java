package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerReaderWriter extends ContainerBase {
    private IReaderWriter readerWriter;

    public ContainerReaderWriter(IReaderWriter readerWriter, EntityPlayer player) {
        super((TileBase) readerWriter, player);

        this.readerWriter = readerWriter;

        addPlayerInventory(8, 127);
    }

    public IReaderWriter getReaderWriter() {
        return readerWriter;
    }
}

package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterListener;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerReaderWriter extends ContainerBase implements IReaderWriterListener {
    private IGuiReaderWriter readerWriter;
    private boolean addedListener;

    public ContainerReaderWriter(IGuiReaderWriter readerWriter, TileBase tile, PlayerEntity player, int windowId) {
        super(RSContainers.READER_WRITER, tile, player, windowId);

        this.readerWriter = readerWriter;

        addPlayerInventory(8, 127);
    }

    public IGuiReaderWriter getReaderWriter() {
        return readerWriter;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        IReaderWriterManager manager = readerWriter.getNetwork() == null ? null : readerWriter.getNetwork().getReaderWriterManager();
        if (!getPlayer().world.isRemote) {
            if (manager != null && !addedListener) {
                manager.addListener(this);

                this.addedListener = true;
            } else if (manager == null && addedListener) {
                this.addedListener = false;
            }
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        IReaderWriterManager manager = readerWriter.getNetwork() == null ? null : readerWriter.getNetwork().getReaderWriterManager();
        if (!player.getEntityWorld().isRemote && manager != null && addedListener) {
            manager.removeListener(this);
        }
    }

    @Override
    public void onAttached() {
        onChanged();
    }

    @Override
    public void onChanged() {
        // TODO RS.INSTANCE.network.sendTo(new MessageReaderWriterUpdate(readerWriter.getNetwork().getReaderWriterManager().getChannels()), (ServerPlayerEntity) getPlayer());
    }
}

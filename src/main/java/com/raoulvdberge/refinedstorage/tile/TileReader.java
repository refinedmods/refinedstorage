package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class TileReader extends TileNode implements IReader, IReaderWriter {
    @Override
    public int getEnergyUsage() {
        return 0; // @TODO
    }

    @Override
    public void updateNode() {
    }

    @Override
    public int getRedstoneStrength() {
        return worldObj.getRedstonePower(pos, getDirection().getOpposite());
    }

    @Override
    public String getTitle() {
        return "gui.refinedstorage:reader";
    }

    @Override
    public void onAdd(String name) {
        if (network != null && !name.isEmpty()) {
            network.addReaderWriterChannel(name);

            network.sendReaderWriterChannelUpdate();
        }
    }

    @Override
    public void onRemove(String name) {
        if (network != null && !name.isEmpty()) {
            network.removeReaderWriterChannel(name);

            network.sendReaderWriterChannelUpdate();
        }
    }

    @Override
    public BlockPos getNetworkPosition() {
        return network != null ? network.getPosition() : null;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    public void onOpened(EntityPlayer entity) {
        if (isConnected()) {
            network.sendReaderWriterChannelUpdate((EntityPlayerMP) entity);
        }
    }
}

package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class TileWriter extends TileNode implements IWriter, IReaderWriter {
    private int redstoneStrength;
    private int lastRedstoneStrength;

    @Override
    public int getEnergyUsage() {
        return 0; // @TODO
    }

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote && redstoneStrength != lastRedstoneStrength) {
            lastRedstoneStrength = redstoneStrength;

            worldObj.notifyNeighborsOfStateChange(pos, RSBlocks.WRITER);
        }
    }

    @Override
    public void updateNode() {
    }

    @Override
    public int getRedstoneStrength() {
        return connected ? redstoneStrength : 0;
    }

    @Override
    public void setRedstoneStrength(int strength) {
        redstoneStrength = strength;
    }

    @Override
    public boolean hasStackUpgrade() {
        return false; // @TODO
    }

    @Override
    public String getTitle() {
        return "gui.refinedstorage:writer";
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

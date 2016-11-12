package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileWriter extends TileNode implements IWriter {
    private static final String NBT_CHANNEL = "Channel";

    private static final TileDataParameter<String> CHANNEL = TileReader.createChannelParameter();

    private String channel = "";

    private int redstoneStrength;
    private int lastRedstoneStrength;

    public TileWriter() {
        dataManager.addWatchedParameter(CHANNEL);
    }

    @Override
    public int getEnergyUsage() {
        return 0; // @TODO
    }

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote && getRedstoneStrength() != lastRedstoneStrength) {
            lastRedstoneStrength = getRedstoneStrength();

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
    public String getTitle() {
        return "gui.refinedstorage:writer";
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public void setChannel(String channel) {
        if (network != null && channel.equals("")) {
            IReaderWriterChannel networkChannel = network.getReaderWriterChannel(this.channel);

            if (networkChannel != null) {
                for (IReaderWriterHandler handler : networkChannel.getHandlers()) {
                    handler.onWriterDisabled(this);
                }
            }
        }

        this.channel = channel;
    }

    @Override
    public TileDataParameter<String> getChannelParameter() {
        return CHANNEL;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_CHANNEL)) {
            channel = tag.getString(NBT_CHANNEL);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setString(NBT_CHANNEL, channel);

        return tag;
    }

    @Override
    public void setDirection(EnumFacing direction) {
        super.setDirection(direction);

        worldObj.notifyNeighborsOfStateChange(pos, RSBlocks.WRITER);
    }

    public void onOpened(EntityPlayer entity) {
        if (isConnected()) {
            network.sendReaderWriterChannelUpdate((EntityPlayerMP) entity);
        }
    }
}

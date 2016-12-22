package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.tile.TileWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class NetworkNodeWriter extends NetworkNode implements IWriter {
    public static final String ID = "writer";

    private static final String NBT_CHANNEL = "Channel";

    private String channel = "";

    private int redstoneStrength;
    private int lastRedstoneStrength;

    public NetworkNodeWriter(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.writerUsage;
    }

    @Override
    public void update() {
        super.update();

        if (getRedstoneStrength() != lastRedstoneStrength) {
            lastRedstoneStrength = getRedstoneStrength();

            holder.world().notifyNeighborsOfStateChange(holder.pos(), RSBlocks.WRITER, true);
        }
    }

    @Override
    public int getRedstoneStrength() {
        return network != null ? redstoneStrength : 0;
    }

    @Override
    public void setRedstoneStrength(int strength) {
        redstoneStrength = strength;
    }

    @Override
    public EnumFacing getDirection() {
        return holder.getDirection();
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
        return TileWriter.CHANNEL;
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
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setString(NBT_CHANNEL, channel);

        return tag;
    }

    public void onOpened(EntityPlayer entity) {
        if (network != null) {
            network.sendReaderWriterChannelUpdate((EntityPlayerMP) entity);
        }
    }
}

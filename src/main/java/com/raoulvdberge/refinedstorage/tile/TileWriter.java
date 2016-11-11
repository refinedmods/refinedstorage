package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class TileWriter extends TileNode implements IWriter, IReaderWriter {
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

            worldObj.notifyNeighborsOfStateChange(pos, RSBlocks.WRITER); // @TODO: Does this need to happen too on orientation change?
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
    public String getChannel() {
        return channel;
    }

    @Override
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public TileDataParameter<String> getChannelParameter() {
        return CHANNEL;
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

    public void onOpened(EntityPlayer entity) {
        if (isConnected()) {
            network.sendReaderWriterChannelUpdate((EntityPlayerMP) entity);
        }
    }
}

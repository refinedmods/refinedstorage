package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.tile.TileReader;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeReader extends NetworkNode implements IReader, IGuiReaderWriter {
    public static final String ID = "reader";

    private static final String NBT_CHANNEL = "Channel";

    private String channel = "";

    public NetworkNodeReader(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.readerUsage;
    }

    @Override
    public int getRedstoneStrength() {
        return world.getRedstonePower(pos.offset(getDirection()), getDirection());
    }

    @Override
    public String getTitle() {
        return "gui.refinedstorage:reader";
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
    public TileDataParameter<String, ?> getChannelParameter() {
        return TileReader.CHANNEL;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileReader.REDSTONE_MODE;
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
}

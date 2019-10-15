package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.block.NodeBlock;
import com.raoulvdberge.refinedstorage.tile.TileWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeWriter extends NetworkNode implements IWriter, IGuiReaderWriter {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "writer");

    private static final String NBT_CHANNEL = "Channel";

    private String channel = "";

    private int redstoneStrength;
    private int lastRedstoneStrength;

    public NetworkNodeWriter(World world, BlockPos pos) {
        super(world, pos);
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

            world.notifyNeighborsOfStateChange(pos, RSBlocks.WRITER);
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
            IReaderWriterChannel networkChannel = network.getReaderWriterManager().getChannel(this.channel);

            if (networkChannel != null) {
                for (IReaderWriterHandler handler : networkChannel.getHandlers()) {
                    handler.onWriterDisabled(this);
                }
            }
        }

        this.channel = channel;
    }

    @Override
    public TileDataParameter<String, ?> getChannelParameter() {
        return TileWriter.CHANNEL;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileWriter.REDSTONE_MODE;
    }

    @Override
    public boolean isActive() {
        return world.getBlockState(pos).get(NodeBlock.CONNECTED);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.contains(NBT_CHANNEL)) {
            channel = tag.getString(NBT_CHANNEL);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putString(NBT_CHANNEL, channel);

        return tag;
    }
}

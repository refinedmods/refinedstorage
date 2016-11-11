package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.gui.GuiReaderWriter;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileReader extends TileNode implements IReader, IReaderWriter {
    private static final String NBT_CHANNEL = "Channel";

    static <T extends TileEntity & IReaderWriter> TileDataParameter<String> createChannelParameter() {
        return new TileDataParameter<>(DataSerializers.STRING, "", new ITileDataProducer<String, T>() {
            @Override
            public String getValue(T tile) {
                return tile.getChannel();
            }
        }, new ITileDataConsumer<String, T>() {
            @Override
            public void setValue(T tile, String value) {
                tile.setChannel(value);

                tile.markDirty();
            }
        }, parameter -> {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiReaderWriter) {
                ((GuiReaderWriter) Minecraft.getMinecraft().currentScreen).updateSelection(parameter.getValue());
            }
        });
    }

    public static final TileDataParameter<String> CHANNEL = createChannelParameter();

    private String channel = "";

    public TileReader() {
        dataManager.addWatchedParameter(CHANNEL);
    }

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

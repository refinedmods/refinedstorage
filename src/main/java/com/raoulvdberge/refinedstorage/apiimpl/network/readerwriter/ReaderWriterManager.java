package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import com.raoulvdberge.refinedstorage.network.MessageReaderWriterUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReaderWriterManager implements IReaderWriterManager {
    private static final String NBT_CHANNELS = "ReaderWriterChannels";
    private static final String NBT_NAME = "Name";

    private INetwork network;
    private Runnable listener;
    private Map<String, IReaderWriterChannel> channels = new HashMap<>();

    public ReaderWriterManager(INetwork network, Runnable listener) {
        this.network = network;
        this.listener = listener;
    }

    @Override
    public void update() {
        for (IReaderWriterChannel channel : channels.values()) {
            for (IReaderWriterHandler handler : channel.getHandlers()) {
                handler.update(channel);
            }
        }
    }

    @Nullable
    @Override
    public IReaderWriterChannel getChannel(String name) {
        return channels.get(name);
    }

    @Override
    public void addChannel(String name) {
        channels.put(name, API.instance().createReaderWriterChannel(name, network));

        listener.run();

        sendUpdate();
    }

    @Override
    public void removeChannel(String name) {
        IReaderWriterChannel channel = getChannel(name);

        if (channel != null) {
            channel.getReaders().forEach(reader -> reader.setChannel(""));
            channel.getWriters().forEach(writer -> writer.setChannel(""));

            channels.remove(name);

            listener.run();

            sendUpdate();
        }
    }

    @Override
    public void sendUpdate() {
        // @todo: Move to a listener system
        network.world().getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> player.openContainer instanceof ContainerReaderWriter &&
                ((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork() != null &&
                network.getPosition().equals(((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork().getPosition()))
            .forEach(this::sendUpdateTo);
    }

    @Override
    public void sendUpdateTo(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageReaderWriterUpdate(new ArrayList<>(channels.keySet())), player);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList readerWriterChannelsList = new NBTTagList();

        for (Map.Entry<String, IReaderWriterChannel> entry : channels.entrySet()) {
            NBTTagCompound channelTag = entry.getValue().writeToNBT(new NBTTagCompound());

            channelTag.setString(NBT_NAME, entry.getKey());

            readerWriterChannelsList.appendTag(channelTag);
        }

        tag.setTag(NBT_CHANNELS, readerWriterChannelsList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_CHANNELS)) {
            NBTTagList readerWriterChannelsList = tag.getTagList(NBT_CHANNELS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < readerWriterChannelsList.tagCount(); ++i) {
                NBTTagCompound channelTag = readerWriterChannelsList.getCompoundTagAt(i);

                String name = channelTag.getString(NBT_NAME);

                IReaderWriterChannel channel = API.instance().createReaderWriterChannel(name, network);

                channel.readFromNBT(channelTag);

                channels.put(name, channel);
            }
        }
    }
}

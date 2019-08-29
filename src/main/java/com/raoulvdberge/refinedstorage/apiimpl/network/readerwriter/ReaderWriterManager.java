package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterListener;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

public class ReaderWriterManager implements IReaderWriterManager {
    private static final String NBT_CHANNELS = "ReaderWriterChannels";
    private static final String NBT_NAME = "Name";

    private INetwork network;
    private Map<String, IReaderWriterChannel> channels = new HashMap<>();
    private Set<IReaderWriterListener> listeners = new HashSet<>();

    public ReaderWriterManager(INetwork network) {
        this.network = network;
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

        listeners.forEach(IReaderWriterListener::onChanged);
    }

    @Override
    public void removeChannel(String name) {
        IReaderWriterChannel channel = getChannel(name);

        if (channel != null) {
            channel.getReaders().forEach(reader -> reader.setChannel(""));
            channel.getWriters().forEach(writer -> writer.setChannel(""));

            channels.remove(name);

            listeners.forEach(IReaderWriterListener::onChanged);
        }
    }

    @Override
    public Collection<String> getChannels() {
        return channels.keySet();
    }

    @Override
    public void addListener(IReaderWriterListener listener) {
        listeners.add(listener);

        listener.onAttached();
    }

    @Override
    public void removeListener(IReaderWriterListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void writeToNbt(CompoundNBT tag) {
        ListNBT readerWriterChannelsList = new ListNBT();

        for (Map.Entry<String, IReaderWriterChannel> entry : channels.entrySet()) {
            CompoundNBT channelTag = entry.getValue().writeToNbt(new CompoundNBT());

            channelTag.putString(NBT_NAME, entry.getKey());

            readerWriterChannelsList.add(channelTag);
        }

        tag.put(NBT_CHANNELS, readerWriterChannelsList);
    }

    @Override
    public void readFromNbt(CompoundNBT tag) {
        if (tag.contains(NBT_CHANNELS)) {
            ListNBT readerWriterChannelsList = tag.getList(NBT_CHANNELS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < readerWriterChannelsList.size(); ++i) {
                CompoundNBT channelTag = readerWriterChannelsList.getCompound(i);

                String name = channelTag.getString(NBT_NAME);

                IReaderWriterChannel channel = API.instance().createReaderWriterChannel(name, network);

                channel.readFromNbt(channelTag);

                channels.put(name, channel);
            }
        }
    }
}

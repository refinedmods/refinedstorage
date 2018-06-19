package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.*;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderWriterChannel implements IReaderWriterChannel {
    private static final String NBT_HANDLER = "Handler_%s";

    private String name;
    private INetwork network;

    private List<IReaderWriterHandler> handlers = new ArrayList<>();

    public ReaderWriterChannel(String name, INetwork network) {
        this.name = name;
        this.network = network;
        this.handlers.addAll(API.instance().getReaderWriterHandlerRegistry().all().stream().map(f -> f.create(null)).collect(Collectors.toList()));
    }

    @Override
    public List<IReaderWriterHandler> getHandlers() {
        return handlers;
    }

    @Override
    public List<IReader> getReaders() {
        return network.getNodeGraph().all().stream()
            .filter(n -> n instanceof IReader && name.equals(((IReader) n).getChannel()))
            .map(n -> (IReader) n)
            .collect(Collectors.toList());
    }

    @Override
    public List<IWriter> getWriters() {
        return network.getNodeGraph().all().stream()
            .filter(n -> n instanceof IWriter && name.equals(((IWriter) n).getChannel()))
            .map(n -> (IWriter) n)
            .collect(Collectors.toList());
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        for (IReaderWriterHandler handler : handlers) {
            tag.setTag(String.format(NBT_HANDLER, handler.getId()), handler.writeToNbt(new NBTTagCompound()));
        }

        return tag;
    }

    @Override
    public void readFromNbt(NBTTagCompound tag) {
        for (IReaderWriterHandler handler : handlers) {
            String id = String.format(NBT_HANDLER, handler.getId());

            if (tag.hasKey(id)) {
                IReaderWriterHandlerFactory factory = API.instance().getReaderWriterHandlerRegistry().get(id);

                if (factory != null) {
                    handlers.add(factory.create(tag.getCompoundTag(id)));
                }
            }
        }
    }
}

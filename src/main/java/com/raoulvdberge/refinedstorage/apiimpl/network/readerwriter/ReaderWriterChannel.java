package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerFactory;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderWriterChannel implements IReaderWriterChannel {
    private static final String NBT_HANDLER_ID = "HandlerID";
    private static final String NBT_HANDLERS = "Handlers";

    private List<IReaderWriterHandler> handlers = new ArrayList<>();

    @Override
    public void addHandlers() {
        handlers.addAll(API.instance().getReaderWriterHandlerRegistry().getFactories().stream().map(f -> f.create(null)).collect(Collectors.toList()));
    }

    @Override
    public List<IReaderWriterHandler> getHandlers() {
        return handlers;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList handlersList = new NBTTagList();

        for (IReaderWriterHandler handler : handlers) {
            NBTTagCompound handlerTag = handler.writeToNBT(new NBTTagCompound());

            handlerTag.setString(NBT_HANDLER_ID, handler.getId());

            handlersList.appendTag(handlerTag);
        }

        tag.setTag(NBT_HANDLERS, handlersList);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (!tag.hasKey(NBT_HANDLER_ID)) {
            return;
        }

        NBTTagList handlersList = tag.getTagList(NBT_HANDLERS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < handlersList.tagCount(); ++i) {
            NBTTagCompound handlerTag = handlersList.getCompoundTagAt(i);

            String id = handlerTag.getString(NBT_HANDLER_ID);

            IReaderWriterHandlerFactory factory = API.instance().getReaderWriterHandlerRegistry().getFactory(id);

            if (factory != null) {
                handlers.add(factory.create(tag));
            }
        }
    }
}

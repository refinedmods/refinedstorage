package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Collections;
import java.util.List;

public class ReaderWriterHandlerRedstone implements IReaderWriterHandler {
    public static final String ID = "redstone";

    @Override
    public void update(IReaderWriterChannel channel) {
        int strength = getStrength(channel);

        for (IWriter writer : channel.getWriters()) {
            writer.setRedstoneStrength(strength);
        }
    }

    private int getStrength(IReaderWriterChannel channel) {
        int strength = 0;

        for (IReader reader : channel.getReaders()) {
            strength += reader.getRedstoneStrength();
        }

        return strength;
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        writer.setRedstoneStrength(0);
    }

    @Override
    public boolean hasCapabilityReader(IReader reader, Capability<?> capability) {
        return false;
    }

    @Override
    public <T> T getCapabilityReader(IReader reader, Capability<T> capability) {
        return null;
    }

    @Override
    public boolean hasCapabilityWriter(IWriter writer, Capability<?> capability) {
        return false;
    }

    @Override
    public <T> T getCapabilityWriter(IWriter writer, Capability<T> capability) {
        return null;
    }

    @Override
    public Object getNullCapability() {
        return null;
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<ITextComponent> getStatusReader(IReader reader, IReaderWriterChannel channel) {
        return getStatus(channel);
    }

    @Override
    public List<ITextComponent> getStatusWriter(IWriter writer, IReaderWriterChannel channel) {
        return getStatus(channel);
    }

    private List<ITextComponent> getStatus(IReaderWriterChannel channel) {
        int strength = getStrength(channel);

        if (strength == 0) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new TextComponentTranslation("misc.refinedstorage:reader_writer.redstone", strength));
    }
}

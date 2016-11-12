package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

public class ReaderWriterHandlerRedstone implements IReaderWriterHandler {
    public static final String ID = "redstone";

    @Override
    public void update(IReaderWriterChannel channel) {
        int strength = 0;

        for (IReader reader : channel.getReaders()) {
            strength += reader.getRedstoneStrength();
        }

        for (IWriter writer : channel.getWriters()) {
            writer.setRedstoneStrength(strength);
        }
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        writer.setRedstoneStrength(0);
    }

    @Override
    public boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability) {
        return false;
    }

    @Override
    public <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability) {
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }
}

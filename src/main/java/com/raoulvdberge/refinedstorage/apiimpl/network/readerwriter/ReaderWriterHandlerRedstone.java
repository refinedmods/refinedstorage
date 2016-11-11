package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

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
    public void onConnectionChange(boolean state) {
        // @TODO: Destroy redstone strength
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }
}

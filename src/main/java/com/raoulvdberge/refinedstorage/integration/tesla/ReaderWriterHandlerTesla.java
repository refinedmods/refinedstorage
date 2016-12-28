package com.raoulvdberge.refinedstorage.integration.tesla;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ReaderWriterHandlerTesla implements IReaderWriterHandler {
    public static final String ID = "tesla";

    private static final String NBT_CONTAINER = "Container";

    private BaseTeslaContainer container;
    private TeslaContainerReader containerReader;
    private TeslaContainerWriter containerWriter;

    public ReaderWriterHandlerTesla(@Nullable NBTTagCompound tag) {
        this.container = new BaseTeslaContainer(4000, 4000, 4000);
        this.containerReader = new TeslaContainerReader(container);
        this.containerWriter = new TeslaContainerWriter(container);

        if (tag != null && tag.hasKey(NBT_CONTAINER)) {
            container.deserializeNBT(tag.getCompoundTag(NBT_CONTAINER));
        }
    }

    @Override
    public void update(IReaderWriterChannel channel) {
        // NO OP
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        // NO OP
    }

    @Override
    public boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability) {
        if (readerWriter instanceof IReader) {
            return capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER;
        } else if (readerWriter instanceof IWriter) {
            return capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_PRODUCER;
        }

        return false;
    }

    @Override
    public <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability) {
        if (readerWriter instanceof IReader || readerWriter instanceof IWriter) {
            if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
                return TeslaCapabilities.CAPABILITY_HOLDER.cast(container);
            } else if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && readerWriter instanceof IReader) {
                return TeslaCapabilities.CAPABILITY_CONSUMER.cast(containerReader);
            } else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && readerWriter instanceof IWriter) {
                return TeslaCapabilities.CAPABILITY_PRODUCER.cast(containerWriter);
            }
        }

        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag(NBT_CONTAINER, container.serializeNBT());

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    private class TeslaContainerReader implements ITeslaHolder, ITeslaConsumer {
        private BaseTeslaContainer parent;

        public TeslaContainerReader(BaseTeslaContainer parent) {
            this.parent = parent;
        }

        @Override
        public long givePower(long power, boolean simulated) {
            return parent.givePower(power, simulated);
        }

        @Override
        public long getStoredPower() {
            return parent.getStoredPower();
        }

        @Override
        public long getCapacity() {
            return parent.getCapacity();
        }
    }

    private class TeslaContainerWriter implements ITeslaHolder, ITeslaProducer {
        private BaseTeslaContainer parent;

        public TeslaContainerWriter(BaseTeslaContainer parent) {
            this.parent = parent;
        }

        @Override
        public long takePower(long power, boolean simulated) {
            return parent.takePower(power, simulated);
        }

        @Override
        public long getStoredPower() {
            return parent.getStoredPower();
        }

        @Override
        public long getCapacity() {
            return parent.getCapacity();
        }
    }
}

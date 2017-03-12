package com.raoulvdberge.refinedstorage.integration.tesla;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
    }

    @Override
    public boolean hasCapabilityReader(IReader reader, Capability<?> capability) {
        return capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER;
    }

    @Override
    public <T> T getCapabilityReader(IReader reader, Capability<T> capability) {
        if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
            return TeslaCapabilities.CAPABILITY_HOLDER.cast(container);
        } else if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
            return TeslaCapabilities.CAPABILITY_CONSUMER.cast(containerReader);
        }

        return null;
    }

    @Override
    public boolean hasCapabilityWriter(IWriter writer, Capability<?> capability) {
        return capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_PRODUCER;
    }

    @Override
    public <T> T getCapabilityWriter(IWriter writer, Capability<T> capability) {
        if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
            return TeslaCapabilities.CAPABILITY_HOLDER.cast(container);
        } else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
            return TeslaCapabilities.CAPABILITY_PRODUCER.cast(containerWriter);
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

    @Override
    public List<ITextComponent> getStatusReader(IReader reader, IReaderWriterChannel channel) {
        return getStatus(containerReader);
    }

    @Override
    public List<ITextComponent> getStatusWriter(IWriter writer, IReaderWriterChannel channel) {
        return getStatus(containerWriter);
    }

    private List<ITextComponent> getStatus(ITeslaHolder holder) {
        if (holder.getStoredPower() == 0) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new TextComponentString(TeslaUtils.getDisplayableTeslaCount(holder.getStoredPower()) + " / " + TeslaUtils.getDisplayableTeslaCount(holder.getCapacity())));
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

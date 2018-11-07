package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerFactory;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileWriter extends TileNode<NetworkNodeWriter> {
    public static final TileDataParameter<String, TileWriter> CHANNEL = TileReader.createChannelParameter();

    public TileWriter() {
        dataManager.addWatchedParameter(CHANNEL);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (super.hasCapability(capability, facing)) {
            return true;
        }

        IWriter writer = getNode();

        if (facing != getDirection()) {
            return false;
        }

        for (IReaderWriterHandlerFactory handlerFactory : API.instance().getReaderWriterHandlerRegistry().all()) {
            if (handlerFactory.create(null).hasCapabilityWriter(writer, capability)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        T foundCapability = super.getCapability(capability, facing);

        if (foundCapability == null) {
            IWriter writer = getNode();

            if (facing != getDirection()) {
                return null;
            }

            Object dummyCap = null;
            for (IReaderWriterHandlerFactory handlerFactory : API.instance().getReaderWriterHandlerRegistry().all()) {
                if (handlerFactory.create(null).hasCapabilityWriter(writer, capability)) {
                    dummyCap = handlerFactory.create(null).getNullCapability();
                }
            }

            if (!writer.canUpdate()) {
                return (T) dummyCap;
            }

            IReaderWriterChannel channel = writer.getNetwork().getReaderWriterManager().getChannel(writer.getChannel());

            if (channel == null) {
                return (T) dummyCap;
            }

            for (IReaderWriterHandler handler : channel.getHandlers()) {
                foundCapability = handler.getCapabilityWriter(writer, capability);

                if (foundCapability != null) {
                    return foundCapability;
                }
            }
        }

        return foundCapability;
    }

    @Override
    @Nonnull
    public NetworkNodeWriter createNode(World world, BlockPos pos) {
        return new NetworkNodeWriter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeWriter.ID;
    }
}

package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileWriter extends NetworkNodeTile<NetworkNodeWriter> {
    public static final TileDataParameter<String, TileWriter> CHANNEL = TileReader.createChannelParameter();

    public TileWriter() {
        super(RSTiles.WRITER);

        dataManager.addWatchedParameter(CHANNEL);
    }

    /* TODO
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
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
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
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
    }*/

    @Override
    @Nonnull
    public NetworkNodeWriter createNode(World world, BlockPos pos) {
        return new NetworkNodeWriter(world, pos);
    }
}

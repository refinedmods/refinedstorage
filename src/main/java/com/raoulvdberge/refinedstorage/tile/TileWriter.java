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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileWriter extends TileNode<NetworkNodeWriter> {
    public static final TileDataParameter<String, TileWriter> CHANNEL = TileReader.createChannelParameter();

    public TileWriter() {
        dataManager.addWatchedParameter(CHANNEL);
    }

    private <T> T getDummyCapabilityForClient(IWriter writer, Capability<T> capability) {
        for (IReaderWriterHandlerFactory factory : API.instance().getReaderWriterHandlerRegistry().all()) {
            T dummy = factory.create(null).getCapabilityWriter(writer, capability);

            if (dummy != null) {
                return dummy;
            }
        }

        return null;
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

        if (writer.getNetwork() == null || !writer.canUpdate()) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                return getDummyCapabilityForClient(writer, capability) != null;
            }

            return false;
        }

        IReaderWriterChannel channel = writer.getNetwork().getReaderWriterManager().getChannel(writer.getChannel());

        if (channel == null) {
            return false;
        }

        for (IReaderWriterHandler handler : channel.getHandlers()) {
            if (handler.hasCapabilityWriter(writer, capability)) {
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

            if (writer.getNetwork() == null || !writer.canUpdate()) {
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    return getDummyCapabilityForClient(writer, capability);
                }

                return null;
            }

            IReaderWriterChannel channel = writer.getNetwork().getReaderWriterManager().getChannel(writer.getChannel());

            if (channel == null) {
                return null;
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

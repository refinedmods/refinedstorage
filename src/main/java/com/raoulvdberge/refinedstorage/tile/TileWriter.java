package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileWriter extends TileNode<NetworkNodeWriter> {
    public static final TileDataParameter<String> CHANNEL = TileReader.createChannelParameter();

    public TileWriter() {
        dataManager.addWatchedParameter(CHANNEL);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (super.hasCapability(capability, facing)) {
            return true;
        }

        IWriter writer = getNode();

        if (facing != getDirection()) {
            return false;
        }

        if (writer.getNetwork() == null || !writer.canUpdate()) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                return TileReader.getDummyCapabilityForClient(writer, capability) != null;
            }

            return false;
        }

        IReaderWriterChannel channel = writer.getNetwork().getReaderWriterChannel(writer.getChannel());

        if (channel == null) {
            return false;
        }

        for (IReaderWriterHandler handler : channel.getHandlers()) {
            if (handler.hasCapability(writer, capability)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T foundCapability = super.getCapability(capability, facing);

        if (foundCapability == null) {
            IWriter writer = getNode();

            if (facing != getDirection()) {
                return null;
            }

            if (writer.getNetwork() == null || !writer.canUpdate()) {
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    return TileReader.getDummyCapabilityForClient(writer, capability);
                }

                return null;
            }

            IReaderWriterChannel channel = writer.getNetwork().getReaderWriterChannel(writer.getChannel());

            if (channel == null) {
                return null;
            }

            for (IReaderWriterHandler handler : channel.getHandlers()) {
                foundCapability = handler.getCapability(writer, capability);

                if (foundCapability != null) {
                    return foundCapability;
                }
            }
        }

        return foundCapability;
    }

    @Override
    @Nonnull
    public NetworkNodeWriter createNode() {
        return new NetworkNodeWriter(this);
    }
}

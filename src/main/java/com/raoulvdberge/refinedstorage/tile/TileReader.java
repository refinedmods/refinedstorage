package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandlerFactory;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeReader;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiReaderWriter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileReader extends TileNode<NetworkNodeReader> {
    static <T extends TileNode> TileDataParameter<String, T> createChannelParameter() {
        return new TileDataParameter<>(DataSerializers.STRING, "", t -> ((IGuiReaderWriter) t.getNode()).getChannel(), (t, v) -> {
            ((IGuiReaderWriter) t.getNode()).setChannel(v);

            t.getNode().markNetworkNodeDirty();
        }, (initial, p) -> GuiBase.executeLater(GuiReaderWriter.class, readerWriter -> readerWriter.setCurrentChannel(p)));
    }

    public static final TileDataParameter<String, TileReader> CHANNEL = createChannelParameter();

    public TileReader() {
        dataManager.addWatchedParameter(CHANNEL);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (super.hasCapability(capability, facing)) {
            return true;
        }

        IReader reader = getNode();

        if (facing != getDirection()) {
            return false;
        }

        for (IReaderWriterHandlerFactory handlerFactory : API.instance().getReaderWriterHandlerRegistry().all()) {
            if (handlerFactory.create(null).hasCapabilityReader(reader, capability)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        T foundCapability = super.getCapability(capability, facing);

        if (foundCapability == null) {
            IReader reader = getNode();

            if (facing != getDirection()) {
                return null;
            }

            Object dummyCap = null;
            for (IReaderWriterHandlerFactory handlerFactory : API.instance().getReaderWriterHandlerRegistry().all()) {
                if (handlerFactory.create(null).hasCapabilityReader(reader, capability)) {
                    dummyCap = handlerFactory.create(null).getNullCapability();
                }
            }

            if (!reader.canUpdate()) {
                return (T) dummyCap;
            }

            IReaderWriterChannel channel = reader.getNetwork().getReaderWriterManager().getChannel(reader.getChannel());

            if (channel == null) {
                return (T) dummyCap;
            }

            for (IReaderWriterHandler handler : channel.getHandlers()) {
                foundCapability = handler.getCapabilityReader(reader, capability);

                if (foundCapability != null) {
                    return foundCapability;
                }
            }
        }

        return foundCapability;
    }

    @Override
    @Nonnull
    public NetworkNodeReader createNode(World world, BlockPos pos) {
        return new NetworkNodeReader(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeReader.ID;
    }
}

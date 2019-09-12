package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeInterface;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileInterface extends TileNode<NetworkNodeInterface> {
    public static final TileDataParameter<Integer, TileInterface> COMPARE = IComparable.createParameter();

    public TileInterface() {
        super(RSTiles.INTERFACE);

        dataManager.addWatchedParameter(COMPARE);
    }

    /* TODO
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getItems()) : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }*/

    @Override
    @Nonnull
    public NetworkNodeInterface createNode(World world, BlockPos pos) {
        return new NetworkNodeInterface(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeInterface.ID;
    }
}

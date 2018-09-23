package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafter;
import com.raoulvdberge.refinedstorage.gui.TileDataParameterClientListenerCrafter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCrafter extends TileNode<NetworkNodeCrafter> {
    public static final TileDataParameter<String, TileCrafter> NAME = new TileDataParameter<>(DataSerializers.STRING, NetworkNodeCrafter.DEFAULT_NAME, t -> t.getNode().getName());
    public static final TileDataParameter<Integer, TileCrafter> MODE = new TileDataParameter<>(DataSerializers.VARINT, NetworkNodeCrafter.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(NetworkNodeCrafter.CrafterMode.getById(v)));
    private static final TileDataParameter<Boolean, TileCrafter> HAS_ROOT = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new TileDataParameterClientListenerCrafter().onChanged(t, v));

    public TileCrafter() {
        dataManager.addWatchedParameter(NAME);
        dataManager.addWatchedParameter(MODE);
        dataManager.addParameter(HAS_ROOT);
    }

    @Override
    @Nonnull
    public NetworkNodeCrafter createNode(World world, BlockPos pos) {
        return new NetworkNodeCrafter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCrafter.ID;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getPatternItems());
        }

        return super.getCapability(capability, facing);
    }
}

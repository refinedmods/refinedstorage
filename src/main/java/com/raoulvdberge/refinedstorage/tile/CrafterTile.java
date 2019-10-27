package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.raoulvdberge.refinedstorage.screen.TileDataParameterClientListenerCrafter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrafterTile extends NetworkNodeTile<CrafterNetworkNode> {
    public static final TileDataParameter<Integer, CrafterTile> MODE = new TileDataParameter<>(DataSerializers.VARINT, CrafterNetworkNode.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(CrafterNetworkNode.CrafterMode.getById(v)));
    private static final TileDataParameter<Boolean, CrafterTile> HAS_ROOT = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new TileDataParameterClientListenerCrafter().onChanged(t, v));

    private LazyOptional<IItemHandler> patternsCapability = LazyOptional.of(() -> getNode().getPatternItems());

    public CrafterTile() {
        super(RSTiles.CRAFTER);

        dataManager.addWatchedParameter(MODE);
        dataManager.addParameter(HAS_ROOT);
    }

    @Override
    @Nonnull
    public CrafterNetworkNode createNode(World world, BlockPos pos) {
        return new CrafterNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return patternsCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}

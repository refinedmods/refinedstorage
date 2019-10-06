package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NetworkNodeTile<N extends NetworkNode> extends BaseTile implements INetworkNodeProxy<N>, IRedstoneConfigurable {
    public static final TileDataParameter<Integer, NetworkNodeTile> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_COVERS = "Cover";

    private N clientNode;

    private LazyOptional<INetworkNodeProxy<N>> networkNodeProxy = LazyOptional.of(() -> this);

    public NetworkNodeTile(TileEntityType<?> tileType) {
        super(tileType);

        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return getNode().getRedstoneMode();
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        getNode().setRedstoneMode(mode);
    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        if (getNode() instanceof ICoverable) {
            tag.put(NBT_COVERS, ((ICoverable) getNode()).getCoverManager().writeToNbt());
        }

        return tag;
    }

    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        if (getNode() instanceof ICoverable && tag.contains(NBT_COVERS)) {
            ((ICoverable) getNode()).getCoverManager().readFromNbt(tag.getList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public N getNode() {
        if (world.isRemote) {
            if (clientNode == null) {
                clientNode = createNode(world, pos);
            }

            return clientNode;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager((ServerWorld) world);

        INetworkNode node = manager.getNode(pos);

        if (node == null) {
            manager.setNode(pos, node = createNode(world, pos));
            manager.markForSaving();
        }

        return (N) node;
    }

    public abstract N createNode(World world, BlockPos pos);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
            return networkNodeProxy.cast();
        }

        return super.getCapability(cap);
    }
}

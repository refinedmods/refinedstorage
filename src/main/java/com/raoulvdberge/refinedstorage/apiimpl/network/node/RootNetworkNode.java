package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RootNetworkNode implements INetworkNode, INetworkNodeVisitor {
    private final INetwork network;
    private final World world;
    private final BlockPos pos;

    public RootNetworkNode(INetwork network, World world, BlockPos pos) {
        this.network = network;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        BlockState state = world.getBlockState(pos);

        @SuppressWarnings("deprecation")
        Item item = Item.getItemFromBlock(state.getBlock());

        return new ItemStack(item, 1);
    }

    @Override
    public void onConnected(INetwork network) {
    }

    @Override
    public void onDisconnected(INetwork network) {
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public void update() {

    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        return tag;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            operator.apply(world, pos.offset(facing), facing.getOpposite());
        }
    }

    @Override
    public boolean equals(Object o) {
        return API.instance().isNetworkNodeEqual(this, o);
    }

    @Override
    public int hashCode() {
        return API.instance().getNetworkNodeHashCode(this);
    }
}

package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor.Operator;

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
    public void setOwner(@Nullable UUID owner) {
        // NO OP
    }

    @Nullable
    @Override
    public UUID getOwner() {
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
        Item item = Item.byBlock(state.getBlock());

        return new ItemStack(item, 1);
    }

    @Override
    public void onConnected(INetwork network) {
        // NO OP
    }

    @Override
    public void onDisconnected(INetwork network) {
        // NO OP
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public void update() {
        // NO OP
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
        // NO OP
    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            operator.apply(world, pos.relative(facing), facing.getOpposite());
        }
    }
}

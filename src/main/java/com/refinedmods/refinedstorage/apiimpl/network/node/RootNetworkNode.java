package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class RootNetworkNode implements INetworkNode, INetworkNodeVisitor {
    private final INetwork network;
    private final Level world;
    private final BlockPos pos;

    public RootNetworkNode(INetwork network, Level world, BlockPos pos) {
        this.network = network;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Nullable
    @Override
    public UUID getOwner() {
        return null;
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        // NO OP
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
    public CompoundTag write(CompoundTag tag) {
        return tag;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public Level getWorld() {
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

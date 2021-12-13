package com.refinedmods.refinedstorage.api.network.node;

import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a node in the network.
 */
public interface INetworkNode {
    /**
     * @return the energy usage of this node
     */
    int getEnergyUsage();

    /**
     * Returns the stack that is displayed in the controller GUI.
     * Can be an empty stack if no stack should be shown.
     *
     * @return the item stack of this node
     */
    @Nonnull
    ItemStack getItemStack();

    /**
     * Called when this node is connected to a network.
     *
     * @param network the network
     */
    void onConnected(INetwork network);

    /**
     * Called when this node is disconnected from a network.
     * This happens when the node is removed, or if the network is removed.
     * If the network runs out of power or no longer runs due to redstone mode settings, this won't be called and has to be detected manually.
     *
     * @param network the network
     */
    void onDisconnected(INetwork network);

    /**
     * Whether this node is active, independent of the network.
     *
     * @return true if this node is active, false otherwise
     */
    boolean isActive();

    /**
     * @return the network, or null if this node is not connected to any network
     */
    @Nullable
    INetwork getNetwork();

    /**
     * Updates a network node.
     */
    void update();

    /**
     * Writes the network node data to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    CompoundTag write(CompoundTag tag);

    /**
     * @return the position of this network node
     */
    BlockPos getPos();

    /**
     * @return the level of this network node
     */
    Level getLevel();

    /**
     * Marks this node as dirty for saving.
     */
    void markDirty();

    /**
     * @return the id of this node as specified in {@link INetworkNodeRegistry}
     */
    ResourceLocation getId();

    /**
     * @param direction the direction
     * @return whether a network signal can be conducted in the given direction.
     */
    default boolean canConduct(Direction direction) {
        return true;
    }

    /**
     * @param direction the direction
     * @return whether the network node can receiver network connections from this side
     */
    default boolean canReceive(Direction direction) {
        return canConduct(direction);
    }

    /**
     * @return the owner
     */
    @Nullable
    UUID getOwner();

    /**
     * @param owner the owner
     */
    void setOwner(@Nullable UUID owner);
}

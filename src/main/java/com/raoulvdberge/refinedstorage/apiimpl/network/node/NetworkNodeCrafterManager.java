package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.block.NodeBlock;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkNodeCrafterManager extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafter_manager");

    private static final String NBT_SIZE = "Size";
    private static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

    private int size = IGrid.SIZE_STRETCH;
    private int searchBoxMode = IGrid.SEARCH_BOX_MODE_NORMAL;

    public NetworkNodeCrafterManager(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.crafterManagerUsage;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void sendTo(ServerPlayerEntity player) {
        if (network != null) {
            // TODO RS.INSTANCE.network.sendTo(new MessageCrafterManagerSlotSizes(network.getCraftingManager().getNamedContainers()), player);
        }
    }

    public int getSize() {
        return world.isRemote ? TileCrafterManager.SIZE.getValue() : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_SIZE, size);
        tag.putInt(NBT_SEARCH_BOX_MODE, searchBoxMode);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_SIZE)) {
            size = tag.getInt(NBT_SIZE);
        }

        if (tag.contains(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInt(NBT_SEARCH_BOX_MODE);
        }
    }

    public int getSearchBoxMode() {
        return world.isRemote ? TileCrafterManager.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    public boolean isActive() {
        return world.getBlockState(pos).get(NodeBlock.CONNECTED);
    }
}

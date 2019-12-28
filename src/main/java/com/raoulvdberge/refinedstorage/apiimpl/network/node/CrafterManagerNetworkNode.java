package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.block.CrafterManagerBlock;
import com.raoulvdberge.refinedstorage.block.NetworkNodeBlock;
import com.raoulvdberge.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterManagerNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafter_manager");

    private static final String NBT_SIZE = "Size";
    private static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

    private int size = IGrid.SIZE_STRETCH;
    private int searchBoxMode = IGrid.SEARCH_BOX_MODE_NORMAL;

    public CrafterManagerNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCrafterManager().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public int getSize() {
        return world.isRemote ? CrafterManagerTile.SIZE.getValue() : size;
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
        return world.isRemote ? CrafterManagerTile.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    public boolean isActiveOnClient() {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof CrafterManagerBlock) {
            return state.get(NetworkNodeBlock.CONNECTED);
        }

        return false;
    }
}

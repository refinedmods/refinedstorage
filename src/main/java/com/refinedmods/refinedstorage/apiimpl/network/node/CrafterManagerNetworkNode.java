package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.block.CrafterManagerBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterManagerNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafter_manager");

    private static final String NBT_SIZE = "Size";
    private static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

    private int size = IGrid.SIZE_STRETCH;
    private int searchBoxMode = IGrid.SEARCH_BOX_MODE_NORMAL;

    public CrafterManagerNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
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
        return level.isClientSide ? CrafterManagerBlockEntity.SIZE.getValue() : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_SIZE, size);
        tag.putInt(NBT_SEARCH_BOX_MODE, searchBoxMode);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_SIZE)) {
            size = tag.getInt(NBT_SIZE);
        }

        if (tag.contains(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInt(NBT_SEARCH_BOX_MODE);
        }
    }

    public int getSearchBoxMode() {
        return level.isClientSide ? CrafterManagerBlockEntity.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    public boolean isActiveOnClient() {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof CrafterManagerBlock) {
            return state.getValue(NetworkNodeBlock.CONNECTED);
        }

        return false;
    }
}

package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.tile.NetworkReceiverTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class NetworkReceiverBlock extends NetworkNodeBlock {
    public NetworkReceiverBlock(ResourceLocation registryName) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(registryName);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NetworkReceiverTile();
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCrafter extends BlockNode {
    public BlockCrafter() {
        super("crafter");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrafter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.CRAFTER, player, world, pos, side);
        }

        return true;
    }

    @Override
    public PlacementType getPlacementType() {
        return PlacementType.ANY_FACE_PLAYER;
    }

    public boolean hasConnectivityState() {
        return true;
    }
}

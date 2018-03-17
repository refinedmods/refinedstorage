package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCrafterManager extends BlockNode {
    public BlockCrafterManager() {
        super("crafter_manager");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrafterManager();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && tryOpenNetworkGui(RSGui.CRAFTER_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.AUTOCRAFTING)) {
            ((TileCrafterManager) world.getTileEntity(pos)).getNode().send((EntityPlayerMP) player);
        }

        return true;
    }
}

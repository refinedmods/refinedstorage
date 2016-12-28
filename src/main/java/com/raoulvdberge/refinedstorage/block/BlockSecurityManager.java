package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeSecurityManager;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSecurityManager extends BlockNode {
    public BlockSecurityManager() {
        super("security_manager");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSecurityManager();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote && placer instanceof EntityPlayer) {
            ((TileSecurityManager) world.getTileEntity(pos)).getNode().setOwner(((EntityPlayer) placer).getGameProfile().getId());
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.getGameProfile().getId().equals(((TileSecurityManager) world.getTileEntity(pos)).getNode().getOwner())) {
                player.openGui(RS.INSTANCE, RSGui.SECURITY_MANAGER, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                tryOpenNetworkGui(RSGui.SECURITY_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.SECURITY);
            }
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

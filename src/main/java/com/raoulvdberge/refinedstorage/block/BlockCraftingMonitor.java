package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCraftingMonitor extends BlockNode {
    public BlockCraftingMonitor() {
        super("crafting_monitor");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCraftingMonitor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.CRAFTING_MONITOR, player, world, pos, side, Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

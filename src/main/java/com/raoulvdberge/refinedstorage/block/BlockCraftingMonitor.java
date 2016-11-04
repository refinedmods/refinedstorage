package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.CRAFTING_MONITOR, world, pos.getX(), pos.getY(), pos.getZ());

            ((TileCraftingMonitor) world.getTileEntity(pos)).onOpened(player);
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

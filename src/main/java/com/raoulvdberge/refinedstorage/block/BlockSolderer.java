package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileSolderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSolderer extends BlockNode {
    public BlockSolderer() {
        super("solderer");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSolderer();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.SOLDERER, player, world, pos, side);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileSolderer && ((TileSolderer) tile).isWorking()) {
            EnumFacing direction = getActualState(state, world, pos).getValue(getDirection().getProperty());

            double x = 0;
            double y = (double) pos.getY() + 0.6D + rand.nextDouble() / 32F;
            double z = 0;

            if (direction == EnumFacing.NORTH) {
                x = (double) pos.getX() + 0.4D;
                z = (double) pos.getZ() + 0.4D;
            } else if (direction == EnumFacing.EAST) {
                x = (double) pos.getX() + 0.6D;
                z = (double) pos.getZ() + 0.4D;
            } else if (direction == EnumFacing.SOUTH) {
                x = (double) pos.getX() + 0.6D;
                z = (double) pos.getZ() + 0.6D;
            } else if (direction == EnumFacing.WEST) {
                x = (double) pos.getX() + 0.4D;
                z = (double) pos.getZ() + 0.6D;
            }

            int particles = rand.nextInt(5);

            for (int i = 0; i < 1 + particles; ++i) {
                world.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    x + (rand.nextDouble() / 16F * (rand.nextBoolean() ? 1 : -1)),
                    y,
                    z + (rand.nextDouble() / 16F * (rand.nextBoolean() ? 1 : -1)),
                    0.0D,
                    0.0D,
                    0.0D
                );
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}

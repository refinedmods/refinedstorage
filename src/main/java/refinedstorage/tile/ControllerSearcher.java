package refinedstorage.tile;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;

import java.util.Set;

public class ControllerSearcher {
    public static TileController search(World world, BlockPos current, Set<String> visited) {
        if (visited.contains(current.getX() + "," + current.getY() + "," + current.getZ())) {
            return null;
        }

        visited.add(current.getX() + "," + current.getY() + "," + current.getZ());

        TileEntity tile = world.getTileEntity(current);

        if (tile instanceof TileController) {
            return (TileController) tile;
        }

        Block block = world.getBlockState(current).getBlock();

        if (tile instanceof TileMachine || block == RefinedStorageBlocks.CABLE) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                TileController controller = search(world, current.offset(dir), visited);

                if (controller != null) {
                    return controller;
                }
            }
        }

        return null;
    }
}

package refinedstorage.tile.controller;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.TileRelay;

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
        } else if (tile instanceof TileMachine) {
            if (visited.size() > 1 && tile instanceof TileRelay && !((TileRelay) tile).mayUpdate()) {
                return null;
            }

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

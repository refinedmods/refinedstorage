package refinedstorage.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            // We need to have visited more than 1 tile so that the relay can find a controller for itself
            if (visited.size() > 1 && tile instanceof TileRelay && !((TileRelay) tile).isConnected()) {
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

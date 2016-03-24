package refinedstorage.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class TileCable extends TileBase {
    public static boolean hasConnectionWith(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof TileCable || tile instanceof TileMachine || tile instanceof TileController;
    }

    public void addMachines(List<BlockPos> visited, List<TileMachine> machines, TileController controller) {
        for (BlockPos visitedBlock : visited) {
            if (visitedBlock.equals(pos)) {
                return;
            }
        }

        visited.add(pos);

        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos newPos = pos.offset(dir);

            boolean alreadyVisited = false;

            for (BlockPos visitedBlock : visited) {
                if (visitedBlock.equals(newPos)) {
                    alreadyVisited = true;
                }
            }

            if (alreadyVisited) {
                continue;
            }

            TileEntity tile = worldObj.getTileEntity(newPos);

            if (tile instanceof TileMachine && ((TileMachine) tile).getRedstoneMode().isEnabled(worldObj, newPos)) {
                machines.add((TileMachine) tile);

                visited.add(newPos);

                if (tile instanceof TileRelay) {
                    for (EnumFacing relayDir : EnumFacing.VALUES) {
                        TileEntity nextToRelay = worldObj.getTileEntity(newPos.offset(relayDir));

                        if (nextToRelay instanceof TileCable) {
                            ((TileCable) nextToRelay).addMachines(visited, machines, controller);
                        }
                    }
                }
            } else if (tile instanceof TileCable) {
                ((TileCable) tile).addMachines(visited, machines, controller);
            } else if (tile instanceof TileController && !controller.getPos().equals(newPos)) {
                worldObj.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.5f, true);
            }
        }
    }
}

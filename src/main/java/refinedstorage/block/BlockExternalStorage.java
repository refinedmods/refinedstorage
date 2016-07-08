package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.tile.externalstorage.TileExternalStorage;

public class BlockExternalStorage extends BlockNode {
    public BlockExternalStorage() {
        super("external_storage");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExternalStorage();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        super.neighborChanged(state, world, pos, block);

        if (!world.isRemote) {
            TileExternalStorage externalStorage = (TileExternalStorage) world.getTileEntity(pos);

            if (externalStorage.getNetwork() != null) {
                externalStorage.updateStorage(externalStorage.getNetwork());
            }
        }
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.ANY;
    }
}

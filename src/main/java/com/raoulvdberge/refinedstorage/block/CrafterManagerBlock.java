package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.container.factory.CrafterManagerContainerProvider;
import com.raoulvdberge.refinedstorage.tile.CrafterManagerTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrafterManagerBlock extends NetworkNodeBlock {
    public CrafterManagerBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "crafter_manager");
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrafterManagerTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attempt(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new CrafterManagerContainerProvider((CrafterManagerTile) world.getTileEntity(pos)),
                buf -> {
                    buf.writeBlockPos(pos);

                    Map<String, List<IItemHandlerModifiable>> containerData = ((CrafterManagerTile) world.getTileEntity(pos)).getNode().getNetwork().getCraftingManager().getNamedContainers();

                    buf.writeInt(containerData.size());

                    for (Map.Entry<String, List<IItemHandlerModifiable>> entry : containerData.entrySet()) {
                        buf.writeString(entry.getKey());

                        int slots = 0;
                        for (IItemHandlerModifiable handler : entry.getValue()) {
                            slots += handler.getSlots();
                        }
                        buf.writeInt(slots);
                    }
                }
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return true;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

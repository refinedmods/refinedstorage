package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.container.InterfaceContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.InterfaceTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class InterfaceBlock extends NodeBlock {
    public InterfaceBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "interface");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InterfaceTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attempt(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<InterfaceTile>(
                    new TranslationTextComponent("gui.refinedstorage.interface"),
                    (tile, windowId, inventory, p) -> new InterfaceContainer(tile, player, windowId),
                    pos
                ),
                pos
            ), Permission.MODIFY, Permission.INSERT, Permission.EXTRACT);
        }

        return true;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

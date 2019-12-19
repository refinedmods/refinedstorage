package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.container.CrafterContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.CrafterTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CrafterBlock extends NetworkNodeBlock {
    public CrafterBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "crafter");
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY_FACE_PLAYER;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrafterTile();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof CrafterTile && stack.hasDisplayName()) {
                ((CrafterTile) tile).getNode().setDisplayName(stack.getDisplayName());
                ((CrafterTile) tile).getNode().markDirty();
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attempt(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<CrafterTile>(
                    ((CrafterTile) world.getTileEntity(pos)).getNode().getName(),
                    (tile, windowId, inventory, p) -> new CrafterContainer(tile, player, windowId),
                    pos
                ),
                pos
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return true;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

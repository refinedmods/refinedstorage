package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import com.raoulvdberge.refinedstorage.container.FluidStorageContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.FluidStorageTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidStorageBlock extends NodeBlock {
    private final FluidStorageType type;

    public FluidStorageBlock(FluidStorageType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;

        this.setRegistryName(RS.ID, type.getName() + "_fluid_storage_block");
    }

    public FluidStorageType getType() {
        return type;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        if (!world.isRemote) {
            FluidStorageNetworkNode storage = ((FluidStorageTile) world.getTileEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUniqueId(FluidStorageNetworkNode.NBT_ID)) {
                storage.setStorageId(stack.getTag().getUniqueId(FluidStorageNetworkNode.NBT_ID));
            }

            storage.loadStorage();
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidStorageTile(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui((ServerPlayerEntity) player, new PositionalTileContainerProvider<FluidStorageTile>(
                ((FluidStorageTile) world.getTileEntity(pos)).getNode().getTitle(),
                (tile, windowId, inventory, p) -> new FluidStorageContainer(tile, player, windowId),
                pos
            ), pos));
        }

        return true;
    }
}

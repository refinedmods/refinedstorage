package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.container.FluidStorageContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidStorageBlock extends NetworkNodeBlock {
    private final FluidStorageType type;

    public FluidStorageBlock(FluidStorageType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
    }

    public FluidStorageType getType() {
        return type;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        if (!world.isClientSide) {
            FluidStorageNetworkNode storage = ((FluidStorageTile) world.getBlockEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUUID(FluidStorageNetworkNode.NBT_ID)) {
                storage.setStorageId(stack.getTag().getUUID(FluidStorageNetworkNode.NBT_ID));
            }

            storage.loadStorage(player instanceof PlayerEntity ? (PlayerEntity) player : null);
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.setPlacedBy(world, pos, state, player, stack);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidStorageTile(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui((ServerPlayerEntity) player, new PositionalTileContainerProvider<FluidStorageTile>(
                ((FluidStorageTile) world.getBlockEntity(pos)).getNode().getTitle(),
                (tile, windowId, inventory, p) -> new FluidStorageContainer(tile, player, windowId),
                pos
            ), pos));
        }

        return ActionResultType.SUCCESS;
    }
}

package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.container.FluidStorageContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.blockentity.FluidStorageBlockEntity;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        if (!level.isClientSide) {
            FluidStorageNetworkNode storage = ((FluidStorageBlockEntity) level.getBlockEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUUID(FluidStorageNetworkNode.NBT_ID)) {
                storage.setStorageId(stack.getTag().getUUID(FluidStorageNetworkNode.NBT_ID));
            }

            storage.loadStorage(player instanceof Player ? (Player) player : null);
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.setPlacedBy(level, pos, state, player, stack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidStorageBlockEntity(type, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui((ServerPlayer) player, new BlockEntityMenuProvider<FluidStorageBlockEntity>(
                ((FluidStorageBlockEntity) level.getBlockEntity(pos)).getNode().getTitle(),
                (blockEntity, windowId, inventory, p) -> new FluidStorageContainerMenu(blockEntity, player, windowId),
                pos
            ), pos));
        }

        return InteractionResult.SUCCESS;
    }
}

package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.container.StorageContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
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

import javax.annotation.Nullable;

public class StorageBlock extends NetworkNodeBlock {
    private final ItemStorageType type;

    public StorageBlock(ItemStorageType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
    }

    public ItemStorageType getType() {
        return type;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (!level.isClientSide) {
            StorageNetworkNode storage = ((StorageBlockEntity) level.getBlockEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUUID(StorageNetworkNode.NBT_ID)) {
                storage.setStorageId(stack.getTag().getUUID(StorageNetworkNode.NBT_ID));
            }

            storage.loadStorage(entity instanceof Player ? (Player) entity : null);
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.setPlacedBy(level, pos, state, entity, stack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(type, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui((ServerPlayer) player, new BlockEntityMenuProvider<StorageBlockEntity>(
                ((StorageBlockEntity) level.getBlockEntity(pos)).getNode().getTitle(),
                (blockEntity, windowId, inventory, p) -> new StorageContainerMenu(blockEntity, player, windowId),
                pos
            ), pos));
        }

        return InteractionResult.SUCCESS;
    }
}

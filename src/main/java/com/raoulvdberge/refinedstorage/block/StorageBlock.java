package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import com.raoulvdberge.refinedstorage.container.StorageContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.StorageTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class StorageBlock extends NodeBlock {
    private final ItemStorageType type;

    public StorageBlock(ItemStorageType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;

        this.setRegistryName(RS.ID, type.getName() + "_storage_block");
    }

    public ItemStorageType getType() {
        return type;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (!world.isRemote) {
            StorageNetworkNode storage = ((StorageTile) world.getTileEntity(pos)).getNode();

            if (stack.hasTag() && stack.getTag().hasUniqueId(StorageNetworkNode.NBT_ID)) {
                storage.setStorageId(stack.getTag().getUniqueId(StorageNetworkNode.NBT_ID));
            }

            storage.loadStorage();
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }

    // TODO Improve this dropping mechanism
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof StorageTile) {
                @SuppressWarnings("deprecation")
                ItemStack drop = new ItemStack(Item.getItemFromBlock(this));

                drop.setTag(new CompoundNBT());
                drop.getTag().putUniqueId(StorageNetworkNode.NBT_ID, ((StorageTile) tile).getNode().getStorageId());

                InventoryHelper.dropItems(world, pos, NonNullList.from(ItemStack.EMPTY, drop));
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new StorageTile(type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui((ServerPlayerEntity) player, new PositionalTileContainerProvider<StorageTile>(
                ((StorageTile) world.getTileEntity(pos)).getNode().getTitle(),
                (tile, windowId, inventory, p) -> new StorageContainer(tile, player, windowId),
                pos
            ), pos));
        }

        return true;
    }
}

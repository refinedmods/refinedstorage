package com.refinedmods.refinedstorage.loottable;

import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.tile.StorageTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class StorageBlockLootFunction implements ILootFunction {
    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        TileEntity tile = lootContext.get(LootParameters.BLOCK_ENTITY);

        // This code needs to work without the node being removed as well.
        // For example: the Destructor calls getDrops before the node has been removed.

        if (tile instanceof StorageTile) {
            StorageNetworkNode removedNode = ((StorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((StorageTile) tile).getNode();
            }

            stack.setTag(new CompoundNBT());
            stack.getTag().putUniqueId(StorageNetworkNode.NBT_ID, removedNode.getStorageId());
        } else if (tile instanceof FluidStorageTile) {
            FluidStorageNetworkNode removedNode = ((FluidStorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((FluidStorageTile) tile).getNode();
            }

            stack.setTag(new CompoundNBT());
            stack.getTag().putUniqueId(FluidStorageNetworkNode.NBT_ID, removedNode.getStorageId());
        }

        return stack;
    }
}

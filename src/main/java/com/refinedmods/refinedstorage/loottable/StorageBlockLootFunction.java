package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.blockentity.FluidStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class StorageBlockLootFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.STORAGE_BLOCK;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        // This code needs to work without the node being removed as well.
        // For example: the Destructor calls getDrops before the node has been removed.

        if (blockEntity instanceof StorageBlockEntity) {
            StorageNetworkNode removedNode = ((StorageBlockEntity) blockEntity).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((StorageBlockEntity) blockEntity).getNode();
            }

            stack.setTag(new CompoundTag());
            stack.getTag().putUUID(StorageNetworkNode.NBT_ID, removedNode.getStorageId());
        } else if (blockEntity instanceof FluidStorageBlockEntity) {
            FluidStorageNetworkNode removedNode = ((FluidStorageBlockEntity) blockEntity).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((FluidStorageBlockEntity) blockEntity).getNode();
            }

            stack.setTag(new CompoundTag());
            stack.getTag().putUUID(FluidStorageNetworkNode.NBT_ID, removedNode.getStorageId());
        }

        return stack;
    }
}

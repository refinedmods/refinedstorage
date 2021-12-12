package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.tile.StorageTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class StorageBlockLootFunction extends LootItemConditionalFunction {
    protected StorageBlockLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext lootContext) {
        BlockEntity tile = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        // This code needs to work without the node being removed as well.
        // For example: the Destructor calls getDrops before the node has been removed.

        if (tile instanceof StorageTile) {
            StorageNetworkNode removedNode = ((StorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((StorageTile) tile).getNode();
            }

            stack.setTag(new CompoundTag());
            stack.getTag().putUUID(StorageNetworkNode.NBT_ID, removedNode.getStorageId());
        } else if (tile instanceof FluidStorageTile) {
            FluidStorageNetworkNode removedNode = ((FluidStorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((FluidStorageTile) tile).getNode();
            }

            stack.setTag(new CompoundTag());
            stack.getTag().putUUID(FluidStorageNetworkNode.NBT_ID, removedNode.getStorageId());
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return RSLootFunctions.getStorageBlock();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<StorageBlockLootFunction> {
        @Override
        public StorageBlockLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            return new StorageBlockLootFunction(conditions);
        }
    }
}

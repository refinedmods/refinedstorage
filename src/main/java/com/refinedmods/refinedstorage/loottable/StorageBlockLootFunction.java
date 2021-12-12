package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.refinedmods.refinedstorage.RSLootFunctions;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.tile.StorageTile;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class StorageBlockLootFunction extends LootFunction {
    protected StorageBlockLootFunction(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext lootContext) {
        TileEntity tile = lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY);

        // This code needs to work without the node being removed as well.
        // For example: the Destructor calls getDrops before the node has been removed.

        if (tile instanceof StorageTile) {
            StorageNetworkNode removedNode = ((StorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((StorageTile) tile).getNode();
            }

            stack.setTag(new CompoundNBT());
            stack.getTag().putUUID(StorageNetworkNode.NBT_ID, removedNode.getStorageId());
        } else if (tile instanceof FluidStorageTile) {
            FluidStorageNetworkNode removedNode = ((FluidStorageTile) tile).getRemovedNode();
            if (removedNode == null) {
                removedNode = ((FluidStorageTile) tile).getNode();
            }

            stack.setTag(new CompoundNBT());
            stack.getTag().putUUID(FluidStorageNetworkNode.NBT_ID, removedNode.getStorageId());
        }

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return RSLootFunctions.getStorageBlock();
    }

    public static class Serializer extends LootFunction.Serializer<StorageBlockLootFunction> {
        @Override
        public StorageBlockLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditions) {
            return new StorageBlockLootFunction(conditions);
        }
    }
}

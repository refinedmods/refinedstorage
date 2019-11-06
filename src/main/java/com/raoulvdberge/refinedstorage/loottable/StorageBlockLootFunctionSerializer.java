package com.raoulvdberge.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class StorageBlockLootFunctionSerializer extends ILootFunction.Serializer<StorageBlockLootFunction> {
    public StorageBlockLootFunctionSerializer() {
        super(new ResourceLocation(RS.ID, "storage_block"), StorageBlockLootFunction.class);
    }

    @Override
    public void serialize(JsonObject jsonObject, StorageBlockLootFunction storageBlockLootFunction, JsonSerializationContext jsonSerializationContext) {

    }

    @Override
    public StorageBlockLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new StorageBlockLootFunction();
    }
}

package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.refinedmods.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class PortableGridBlockLootFunctionSerializer extends ILootFunction.Serializer<PortableGridBlockLootFunction> {
    public PortableGridBlockLootFunctionSerializer() {
        super(new ResourceLocation(RS.ID, "portable_grid"), PortableGridBlockLootFunction.class);
    }

    @Override
    public void serialize(JsonObject jsonObject, PortableGridBlockLootFunction function, JsonSerializationContext jsonSerializationContext) {

    }

    @Override
    public PortableGridBlockLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new PortableGridBlockLootFunction();
    }
}

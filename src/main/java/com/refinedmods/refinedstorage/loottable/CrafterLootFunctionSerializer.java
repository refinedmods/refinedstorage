package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.refinedmods.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class CrafterLootFunctionSerializer extends ILootFunction.Serializer<CrafterLootFunction> {
    public CrafterLootFunctionSerializer() {
        super(new ResourceLocation(RS.ID, "crafter"), CrafterLootFunction.class);
    }

    @Override
    public void serialize(JsonObject jsonObject, CrafterLootFunction crafterLootFunction, JsonSerializationContext jsonSerializationContext) {

    }

    @Override
    public CrafterLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new CrafterLootFunction();
    }
}

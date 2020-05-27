package com.refinedmods.refinedstorage.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.refinedmods.refinedstorage.RS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class ControllerLootFunctionSerializer extends ILootFunction.Serializer<ControllerLootFunction> {
    public ControllerLootFunctionSerializer() {
        super(new ResourceLocation(RS.ID, "controller"), ControllerLootFunction.class);
    }

    @Override
    public void serialize(JsonObject jsonObject, ControllerLootFunction controllerLootFunction, JsonSerializationContext jsonSerializationContext) {

    }

    @Override
    public ControllerLootFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new ControllerLootFunction();
    }
}

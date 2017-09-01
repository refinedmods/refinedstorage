package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.raoulvdberge.refinedstorage.api.solderer.ISoldererRecipe;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoldererRecipeFactory {
    private final ResourceLocation name;
    private JsonObject json;

    public SoldererRecipeFactory(ResourceLocation name, JsonObject json) {
        this.name = name;
        this.json = json;
    }

    public ISoldererRecipe create(JsonContext context) {
        JsonArray rowsArray = JsonUtils.getJsonArray(json, "rows");

        if (rowsArray.size() != 3) {
            throw new JsonSyntaxException("Expected 3 rows, got " + rowsArray.size() + " rows");
        }

        List<NonNullList<ItemStack>> rows = new ArrayList<>(3);

        for (JsonElement element : rowsArray) {
            if (element.isJsonNull()) {
                rows.add(StackUtils.emptyNonNullList());
            } else {
                rows.add(NonNullList.from(ItemStack.EMPTY, CraftingHelper.getIngredient(element, context).getMatchingStacks()));
            }
        }

        final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
        final int duration = JsonUtils.getInt(json, "duration");
        final boolean projectERecipe = JsonUtils.getBoolean(json, "projecte", true);

        return new ISoldererRecipe() {
            @Override
            public ResourceLocation getName() {
                return name;
            }

            @Nonnull
            @Override
            public NonNullList<ItemStack> getRow(int row) {
                return rows.get(row);
            }

            @Nonnull
            @Override
            public ItemStack getResult() {
                return result;
            }

            @Override
            public int getDuration() {
                return duration;
            }

            @Override
            public boolean isProjectERecipe() {
                return projectERecipe;
            }
        };
    }
}

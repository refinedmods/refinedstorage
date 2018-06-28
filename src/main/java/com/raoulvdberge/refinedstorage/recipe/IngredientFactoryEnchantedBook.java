package com.raoulvdberge.refinedstorage.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

public class IngredientFactoryEnchantedBook implements IIngredientFactory {
    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json) {
        String id = JsonUtils.getString(json, "id");
        int level = JsonUtils.getInt(json, "level", 1);

        Enchantment enchantment = Enchantment.getEnchantmentByLocation(id);

        if (enchantment == null) {
            throw new JsonSyntaxException("Couldn't find enchantment with id '" + id + "'");
        }

        return Ingredient.fromStacks(ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, level)));
    }
}

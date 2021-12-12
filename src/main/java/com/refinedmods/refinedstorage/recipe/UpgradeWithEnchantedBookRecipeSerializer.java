package com.refinedmods.refinedstorage.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class UpgradeWithEnchantedBookRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradeWithEnchantedBookRecipe> {
    @Override
    public UpgradeWithEnchantedBookRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject enchantmentInfo = json.getAsJsonObject("enchantment");

        ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsJsonPrimitive("result").getAsString())));
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentInfo.getAsJsonPrimitive("id").getAsString()));

        int level = 1;
        if (enchantmentInfo.has("level")) {
            level = enchantmentInfo.getAsJsonPrimitive("level").getAsInt();
        }

        return new UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result);
    }

    @Nullable
    @Override
    public UpgradeWithEnchantedBookRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ItemStack result = buffer.readItem();
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
        int level = buffer.readInt();

        return new UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, UpgradeWithEnchantedBookRecipe recipe) {
        buffer.writeItem(recipe.getResult());
        buffer.writeResourceLocation(recipe.getEnchant().enchantment.getRegistryName());
        buffer.writeInt(recipe.getEnchant().level);
    }
}

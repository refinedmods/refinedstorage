package com.refinedmods.refinedstorage.recipe;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class UpgradeWithEnchantedBookRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<UpgradeWithEnchantedBookRecipe> {
    @Override
    public UpgradeWithEnchantedBookRecipe read(ResourceLocation recipeId, JsonObject json) {
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
    public UpgradeWithEnchantedBookRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack result = buffer.readItemStack();
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
        int level = buffer.readInt();

        return new UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result);
    }

    @Override
    public void write(PacketBuffer buffer, UpgradeWithEnchantedBookRecipe recipe) {
        buffer.writeItemStack(recipe.getResult());
        buffer.writeResourceLocation(recipe.getEnchant().enchantment.getRegistryName());
        buffer.writeInt(recipe.getEnchant().enchantmentLevel);
    }
}

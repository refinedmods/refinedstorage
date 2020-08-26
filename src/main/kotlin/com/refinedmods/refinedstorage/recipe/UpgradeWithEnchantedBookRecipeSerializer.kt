package com.refinedmods.refinedstorage.recipe

import com.google.gson.JsonObject
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.ForgeRegistryEntry

class UpgradeWithEnchantedBookRecipeSerializer : ForgeRegistryEntry<IRecipeSerializer<*>?>(), IRecipeSerializer<UpgradeWithEnchantedBookRecipe?> {
    fun read(recipeId: Identifier?, json: JsonObject): UpgradeWithEnchantedBookRecipe {
        val enchantmentInfo = json.getAsJsonObject("enchantment")
        val result = ItemStack(ForgeRegistries.ITEMS.getValue(Identifier(json.getAsJsonPrimitive("result").asString)))
        val enchantment: Enchantment = ForgeRegistries.ENCHANTMENTS.getValue(Identifier(enchantmentInfo.getAsJsonPrimitive("id").asString))
        var level = 1
        if (enchantmentInfo.has("level")) {
            level = enchantmentInfo.getAsJsonPrimitive("level").asInt
        }
        return UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result)
    }

    @Nullable
    fun read(recipeId: Identifier?, buffer: PacketByteBuf): UpgradeWithEnchantedBookRecipe {
        val result: ItemStack = buffer.readItemStack()
        val enchantment: Enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readIdentifier())
        val level: Int = buffer.readInt()
        return UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result)
    }

    fun write(buffer: PacketByteBuf, recipe: UpgradeWithEnchantedBookRecipe) {
        buffer.writeItemStack(recipe.result)
        buffer.writeIdentifier(recipe.enchant.enchantment.getRegistryName())
        buffer.writeInt(recipe.enchant.enchantmentLevel)
    }
}
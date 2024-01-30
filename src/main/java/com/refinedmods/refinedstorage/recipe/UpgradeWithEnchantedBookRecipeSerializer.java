package com.refinedmods.refinedstorage.recipe;

import java.util.Objects;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;

public class UpgradeWithEnchantedBookRecipeSerializer implements RecipeSerializer<UpgradeWithEnchantedBookRecipe> {
    @Override
    public Codec<UpgradeWithEnchantedBookRecipe> codec() {
        return UpgradeWithEnchantedBookRecipe.CODEC;
    }

    @Override
    public UpgradeWithEnchantedBookRecipe fromNetwork(final FriendlyByteBuf buf) {
        final Holder<Item> result = BuiltInRegistries.ITEM.getHolder(ResourceKey.create(
            Registries.ITEM,
            buf.readResourceLocation()
        )).orElseThrow();
        final Holder<Enchantment> enchantment = BuiltInRegistries.ENCHANTMENT.getHolder(ResourceKey.create(
            Registries.ENCHANTMENT,
            buf.readResourceLocation()
        )).orElseThrow();
        final int level = buf.readInt();
        return new UpgradeWithEnchantedBookRecipe(enchantment, level, result);
    }

    @Override
    public void toNetwork(final FriendlyByteBuf buf, final UpgradeWithEnchantedBookRecipe recipe) {
        buf.writeResourceLocation(recipe.getResultItem().unwrapKey().orElseThrow().location());
        buf.writeResourceLocation(Objects.requireNonNull(recipe.getEnchantmentId()));
        buf.writeInt(recipe.getEnchantmentLevel());
    }
}

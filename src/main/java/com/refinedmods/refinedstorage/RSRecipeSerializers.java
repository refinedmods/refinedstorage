package com.refinedmods.refinedstorage;

import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import com.refinedmods.refinedstorage.recipe.UpgradeWithEnchantedBookRecipeSerializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RSRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, RS.ID);

    static {
        REGISTRY.register("upgrade_with_enchanted_book", UpgradeWithEnchantedBookRecipeSerializer::new);
        REGISTRY.register("cover_recipe", () -> CoverRecipe.SERIALIZER);
        REGISTRY.register("hollow_cover_recipe", () -> HollowCoverRecipe.SERIALIZER);
    }

    private RSRecipeSerializers() {
    }
}

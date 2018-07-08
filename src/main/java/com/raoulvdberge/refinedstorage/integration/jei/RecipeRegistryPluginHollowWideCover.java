package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class RecipeRegistryPluginHollowWideCover implements IRecipeRegistryPlugin {
    @Override
    public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
        if (focus.getValue() instanceof ItemStack) {
            ItemStack stack = (ItemStack) focus.getValue();

            if (focus.getMode() == IFocus.Mode.INPUT) {
                if (stack.getItem() == RSItems.COVER && CoverManager.isValidCover(ItemCover.getItem(stack))) {
                    return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
                }
            } else if (focus.getMode() == IFocus.Mode.OUTPUT) {
                if (stack.getItem() == RSItems.HOLLOW_WIDE_COVER) {
                    return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        if (focus.getValue() instanceof ItemStack) {
            ItemStack stack = (ItemStack) focus.getValue();

            if (focus.getMode() == IFocus.Mode.INPUT) {
                if (stack.getItem() == RSItems.COVER && CoverManager.isValidCover(ItemCover.getItem(stack))) {
                    ItemStack hollowWideCover = new ItemStack(RSItems.HOLLOW_WIDE_COVER);

                    ItemCover.setItem(hollowWideCover, ItemCover.getItem(stack));

                    return Collections.singletonList((T) new RecipeWrapperHollowWideCover(stack, hollowWideCover));
                }
            } else if (focus.getMode() == IFocus.Mode.OUTPUT) {
                if (stack.getItem() == RSItems.HOLLOW_WIDE_COVER) {
                    ItemStack cover = new ItemStack(RSItems.COVER);

                    ItemCover.setItem(cover, ItemCover.getItem(stack));

                    return Collections.singletonList((T) new RecipeWrapperHollowWideCover(cover, stack));
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
        return Collections.emptyList();
    }
}

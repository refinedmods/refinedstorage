package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import com.raoulvdberge.refinedstorage.item.ItemHollowCover;
import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class RecipeRegistryPluginHollowCover implements IRecipeRegistryPlugin {
    @Override
    public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
        if (focus.getValue() instanceof ItemStack) {
            ItemStack stack = (ItemStack) focus.getValue();

            if (focus.getMode() == IFocus.Mode.INPUT) {
                if (stack.getItem() == RSItems.COVER) {
                    ItemStack itemInCover = ItemCover.getItem(stack);

                    if ((!RS.INSTANCE.config.hideCovers && CoverManager.isValidCover(itemInCover)) || API.instance().getComparer().isEqualNoQuantity(itemInCover, ItemHollowCover.HIDDEN_COVER_ALTERNATIVE)) {
                        return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
                    }
                }
            } else if (focus.getMode() == IFocus.Mode.OUTPUT) {
                if (stack.getItem() == RSItems.HOLLOW_COVER) {
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
                if (stack.getItem() == RSItems.COVER) {
                    ItemStack itemInCover = ItemCover.getItem(stack);

                    if ((!RS.INSTANCE.config.hideCovers && CoverManager.isValidCover(itemInCover)) || API.instance().getComparer().isEqualNoQuantity(itemInCover, ItemHollowCover.HIDDEN_COVER_ALTERNATIVE)) {
                        ItemStack hollowCover = new ItemStack(RSItems.HOLLOW_COVER);

                        ItemCover.setItem(hollowCover, itemInCover);

                        return Collections.singletonList((T) new RecipeWrapperHollowCover(stack, hollowCover));
                    }
                }
            } else if (focus.getMode() == IFocus.Mode.OUTPUT) {
                if (stack.getItem() == RSItems.HOLLOW_COVER) {
                    ItemStack cover = new ItemStack(RSItems.COVER);

                    ItemCover.setItem(cover, ItemCover.getItem(stack));

                    return Collections.singletonList((T) new RecipeWrapperHollowCover(cover, stack));
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

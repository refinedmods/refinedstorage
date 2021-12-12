package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.recipe.CoverRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoverCraftingCategoryExtension implements ICustomCraftingCategoryExtension {

    @Override
    public void setIngredients(IIngredients ingredients) {
        List<ItemStack> input = new ArrayList<>();
        List<ItemStack> output = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            Item item = Item.byBlock(block);
            if (item == Items.AIR) {
                continue;
            }
            NonNullList<ItemStack> subBlocks = NonNullList.create();
            block.fillItemCategory(ItemGroup.TAB_SEARCH, subBlocks);
            for (ItemStack subBlock : subBlocks) {
                if (CoverManager.isValidCover(subBlock)) {
                    input.add(subBlock);
                    ItemStack stack = new ItemStack(RSItems.COVER.get());
                    CoverItem.setItem(stack, subBlock);
                    output.add(stack);
                }
            }
        }
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(Tags.Items.NUGGETS_IRON.getValues().stream().map(ItemStack::new).collect(Collectors.toList()), input));
        ingredients.setOutputs(VanillaTypes.ITEM, output);
    }

    @Nullable
    @Override
    public Size2i getSize() {
        return new Size2i(2, 1);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return CoverRecipe.SERIALIZER.getRegistryName();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        ItemStack stack = recipeLayout.getFocus(VanillaTypes.ITEM).getValue();
        if (stack.getItem() instanceof CoverItem){
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(4, Tags.Items.NUGGETS_IRON.getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(5, CoverItem.getItem(stack));
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(0, stack);
        } else {
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(4, Tags.Items.NUGGETS_IRON.getValues().stream().map(ItemStack::new).collect(Collectors.toList()));
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(5, stack);
            ItemStack output = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(output, stack);
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(0, output);
        }
    }
}

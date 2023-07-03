package com.refinedmods.refinedstorage.recipe;


import com.google.common.collect.Lists;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.item.CoverItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.List;

public class CoverRecipe extends CustomRecipe {
    public static RecipeSerializer<CoverRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(CoverRecipe::new);

    public CoverRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public static boolean stackMatches(ItemStack first) {
        return CoverManager.isValidCover(first);
    }

    public static boolean matches(List<ItemStack> list) {
        return list.size() == 2;
    }

    public static ItemStack getResult(List<ItemStack> list) {
        if (list.size() == 2) {
            ItemStack first = list.get(0);
            ItemStack second = list.get(1);
            return getResult(first, second);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getResult(ItemStack first, ItemStack second) {
        if (first.is(Tags.Items.NUGGETS_IRON)) {
            ItemStack stack = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(stack, second);
            stack.setCount(6);
            return stack;
        }
        if (second.is(Tags.Items.NUGGETS_IRON)) {
            ItemStack stack = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(stack, first);
            stack.setCount(6);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> list = Lists.newArrayList();
        int ingots = 0;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack = craftingContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (itemstack.is(Tags.Items.NUGGETS_IRON)) {
                    ++ingots;
                } else if (!stackMatches(itemstack)) {
                    return false;
                }
            }
        }
        return matches(list) && ingots == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        List<ItemStack> list = Lists.newArrayList();
        int ingots = 0;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (itemstack.is(Tags.Items.NUGGETS_IRON)) {
                    ++ingots;
                } else if (!stackMatches(itemstack)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (ingots > 1) {
            return ItemStack.EMPTY;
        }
        return getResult(list);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}

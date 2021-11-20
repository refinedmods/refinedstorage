package com.refinedmods.refinedstorage.recipe;


import com.google.common.collect.Lists;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.item.WrenchItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.List;

public class CoverRecipe extends SpecialRecipe {

    public static IRecipeSerializer<CoverRecipe> SERIALIZER = new SpecialRecipeSerializer<>(CoverRecipe::new);

    public CoverRecipe(ResourceLocation idIn) {
        super(idIn);
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

    public static ItemStack getResult(ItemStack first, ItemStack second){
        if (first.getItem().isIn(Tags.Items.NUGGETS_IRON)){
            ItemStack stack = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(stack, second);
            stack.setCount(6);
            return stack;
        }
        if (second.getItem().isIn(Tags.Items.NUGGETS_IRON)){
            ItemStack stack = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(stack, first);
            stack.setCount(6);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        List<ItemStack> list = Lists.newArrayList();
        int ingots = 0;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (itemstack.getItem().isIn(Tags.Items.NUGGETS_IRON)){
                    ++ingots;
                } else if (!stackMatches(itemstack)){
                    return false;
                }
            }
        }
        return matches(list) && ingots == 1;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        List<ItemStack> list = Lists.newArrayList();
        int ingots = 0;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (itemstack.getItem().isIn(Tags.Items.NUGGETS_IRON)){
                    ++ingots;
                } else if (!stackMatches(itemstack)){
                    return ItemStack.EMPTY;
                }
            }
        }
        if (ingots > 1){
            return ItemStack.EMPTY;
        }
        return getResult(list);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}

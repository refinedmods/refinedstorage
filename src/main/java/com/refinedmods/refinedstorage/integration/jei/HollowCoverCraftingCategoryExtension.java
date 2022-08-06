package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HollowCoverCraftingCategoryExtension implements ICraftingCategoryExtension {

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        List<List<ItemStack>> stacks = new ArrayList<>(Collections.nCopies(9, new ArrayList<>()));
        List<ItemStack> input = new ArrayList<>();
        List<ItemStack> output = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            Item item = Item.BY_BLOCK.get(block);
            if (item == null || item == Items.AIR) {
                continue;
            }
            NonNullList<ItemStack> subBlocks = NonNullList.create();
            block.fillItemCategory(CreativeModeTab.TAB_SEARCH, subBlocks);
            for (ItemStack subBlock : subBlocks) {
                if (CoverManager.isValidCover(subBlock)) {
                    ItemStack fullCover = new ItemStack(RSItems.COVER.get());
                    CoverItem.setItem(fullCover, subBlock);
                    input.add(fullCover);
                    ItemStack hollowCover = new ItemStack(RSItems.HOLLOW_COVER.get());
                    CoverItem.setItem(hollowCover, subBlock);
                    output.add(hollowCover);
                }
            }
        }

        stacks.set(4, input);
        List<IRecipeSlotBuilder> inputSlots = craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, stacks, 0, 0);
        IRecipeSlotBuilder outputSlot = craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, output);
        builder.createFocusLink(inputSlots.get(4), outputSlot);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return ForgeRegistries.RECIPE_SERIALIZERS.getKey(HollowCoverRecipe.SERIALIZER);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}

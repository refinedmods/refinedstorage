package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.ProcessorItem;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        //Tag + Color -> Colored Block
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) -> {
            map.forEach((color, item) -> {
                ShapelessRecipeBuilder.shapelessRecipe(item.get())
                    .addIngredient(tag)
                    .addIngredient(color.getTag())
                    .setGroup(RS.ID)
                    .addCriterion("refinedstorage:controller", InventoryChangeTrigger.Instance.forItems(RSItems.CONTROLLER.get(BlockUtils.DEFAULT_COLOR).get()))
                    .build(consumer, new ResourceLocation(RS.ID, "coloring_recipes/" + item.getId().getPath()));
            });
        });

        //Crafting Grid
        RSItems.CRAFTING_GRID.forEach((color, item) -> {
            ShapelessRecipeBuilder.shapelessRecipe(item.get())
                .addIngredient(RSItems.GRID.get(color).get())
                .addIngredient(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
                .addIngredient(ItemTags.makeWrapperTag("refinedstorage:crafting_tables"))
                .addCriterion("refinedstorage:grid", InventoryChangeTrigger.Instance.forItems(RSItems.GRID.get(BlockUtils.DEFAULT_COLOR).get()))
                .build(consumer, new ResourceLocation(RS.ID, "crafting_grid/" + item.getId().getPath()));
        });

        //Fluid Grid
        RSItems.FLUID_GRID.forEach((color, item) -> {
            ShapelessRecipeBuilder.shapelessRecipe(item.get())
                .addIngredient(RSItems.GRID.get(color).get())
                .addIngredient(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
                .addIngredient(Items.BUCKET)
                .addCriterion("refinedstorage:grid", InventoryChangeTrigger.Instance.forItems(RSItems.GRID.get(BlockUtils.DEFAULT_COLOR).get()))
                .build(consumer, new ResourceLocation(RS.ID, "fluid_grid/" + item.getId().getPath()));
        });

        //Pattern Grid
        RSItems.PATTERN_GRID.forEach((color, item) -> {
            ShapelessRecipeBuilder.shapelessRecipe(item.get())
                .addIngredient(RSItems.GRID.get(color).get())
                .addIngredient(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
                .addIngredient(RSItems.PATTERN.get())
                .addCriterion("refinedstorage:grid", InventoryChangeTrigger.Instance.forItems(RSItems.GRID.get(BlockUtils.DEFAULT_COLOR).get()))
                .build(consumer, new ResourceLocation(RS.ID, "pattern_grid/" + item.getId().getPath()));
        });
    }
}

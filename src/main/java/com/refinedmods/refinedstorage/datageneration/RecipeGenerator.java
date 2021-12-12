package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.ProcessorItem;
import com.refinedmods.refinedstorage.util.ColorMap;
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
    private static final String GRID_ID = RS.ID + ":grid";

    public RecipeGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> recipeAcceptor) {
        // Tag + Color -> Colored Block
        RSItems.COLORED_ITEM_TAGS.forEach((tag, map) -> map.forEach((color, item) -> ShapelessRecipeBuilder.shapeless(item.get())
            .requires(tag)
            .requires(color.getTag())
            .group(RS.ID)
            .unlockedBy("refinedstorage:controller", InventoryChangeTrigger.Instance.hasItems(RSItems.CONTROLLER.get(ColorMap.DEFAULT_COLOR).get()))
            .save(recipeAcceptor, new ResourceLocation(RS.ID, "coloring_recipes/" + item.getId().getPath()))
        ));

        // Crafting Grid
        RSItems.CRAFTING_GRID.forEach((color, item) -> ShapelessRecipeBuilder.shapeless(item.get())
            .requires(RSItems.GRID.get(color).get())
            .requires(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
            .requires(ItemTags.bind("refinedstorage:crafting_tables"))
            .unlockedBy(GRID_ID, InventoryChangeTrigger.Instance.hasItems(RSItems.GRID.get(ColorMap.DEFAULT_COLOR).get()))
            .save(recipeAcceptor, new ResourceLocation(RS.ID, "crafting_grid/" + item.getId().getPath()))
        );

        // Fluid Grid
        RSItems.FLUID_GRID.forEach((color, item) -> ShapelessRecipeBuilder.shapeless(item.get())
            .requires(RSItems.GRID.get(color).get())
            .requires(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
            .requires(Items.BUCKET)
            .unlockedBy(GRID_ID, InventoryChangeTrigger.Instance.hasItems(RSItems.GRID.get(ColorMap.DEFAULT_COLOR).get()))
            .save(recipeAcceptor, new ResourceLocation(RS.ID, "fluid_grid/" + item.getId().getPath()))
        );

        // Pattern Grid
        RSItems.PATTERN_GRID.forEach((color, item) -> ShapelessRecipeBuilder.shapeless(item.get())
            .requires(RSItems.GRID.get(color).get())
            .requires(RSItems.PROCESSORS.get(ProcessorItem.Type.ADVANCED).get())
            .requires(RSItems.PATTERN.get())
            .unlockedBy(GRID_ID, InventoryChangeTrigger.Instance.hasItems(RSItems.GRID.get(ColorMap.DEFAULT_COLOR).get()))
            .save(recipeAcceptor, new ResourceLocation(RS.ID, "pattern_grid/" + item.getId().getPath()))
        );
    }
}

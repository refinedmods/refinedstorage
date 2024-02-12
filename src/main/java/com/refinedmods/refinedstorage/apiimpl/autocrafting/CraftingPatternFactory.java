package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.item.PatternItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class CraftingPatternFactory {
    public static final CraftingPatternFactory INSTANCE = new CraftingPatternFactory();

    public ICraftingPattern create(Level level, ICraftingPatternContainer container, ItemStack stack) {
        CraftingPatternContext context = new CraftingPatternContext(container, stack);

        boolean processing = PatternItem.isProcessing(stack);
        boolean exact = PatternItem.isExact(stack);
        AllowedTagList allowedTagList = PatternItem.getAllowedTags(stack);

        List<NonNullList<ItemStack>> inputs = new ArrayList<>();
        NonNullList<ItemStack> outputs = NonNullList.create();
        NonNullList<ItemStack> byproducts = NonNullList.create();
        List<NonNullList<FluidStack>> fluidInputs = new ArrayList<>();
        NonNullList<FluidStack> fluidOutputs = NonNullList.create();
        CraftingRecipe recipe = null;

        try {
            if (processing) {
                for (int i = 0; i < GridNetworkNode.PROCESSING_MATRIX_SIZE; ++i) {
                    fillProcessingInputs(i, stack, inputs, outputs, allowedTagList);
                    fillProcessingFluidInputs(i, stack, fluidInputs, fluidOutputs, allowedTagList);
                }

                if (outputs.isEmpty() && fluidOutputs.isEmpty()) {
                    throw new CraftingPatternFactoryException(
                        Component.translatable("misc.refinedstorage.pattern.error.processing_no_outputs"));
                }
            } else {
                CraftingContainer craftingContainer = new CraftingPattern.DummyCraftingContainer();

                for (int i = 0; i < 9; ++i) {
                    fillCraftingInputs(craftingContainer, stack, inputs, i);
                }

                Optional<CraftingRecipe> foundRecipe =
                    level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level)
                        .map(RecipeHolder::value);
                if (foundRecipe.isPresent()) {
                    recipe = foundRecipe.get();

                    byproducts = recipe.getRemainingItems(craftingContainer);

                    ItemStack output = recipe.assemble(craftingContainer, level.registryAccess());

                    if (!output.isEmpty()) {
                        outputs.add(output);

                        if (!exact) {
                            modifyCraftingInputsToUseAlternatives(recipe, inputs);
                        }
                    } else {
                        throw new CraftingPatternFactoryException(
                            Component.translatable("misc.refinedstorage.pattern.error.no_output"));
                    }
                } else {
                    throw new CraftingPatternFactoryException(
                        Component.translatable("misc.refinedstorage.pattern.error.recipe_does_not_exist"));
                }
            }
        } catch (CraftingPatternFactoryException e) {
            return new InvalidCraftingPattern(context, e.getErrorMessage());
        }

        return new CraftingPattern(
            context,
            processing,
            exact,
            recipe,
            new CraftingPatternInputs(inputs, fluidInputs),
            new CraftingPatternOutputs(outputs, byproducts, fluidOutputs),
            allowedTagList
        );
    }

    private void fillProcessingInputs(int i, ItemStack stack, List<NonNullList<ItemStack>> inputs,
                                      NonNullList<ItemStack> outputs, @Nullable AllowedTagList allowedTagList)
        throws CraftingPatternFactoryException {
        ItemStack input = PatternItem.getInputSlot(stack, i);

        if (input.isEmpty()) {
            inputs.add(NonNullList.create());
        } else {
            NonNullList<ItemStack> possibilities = NonNullList.create();

            possibilities.add(input.copy());

            if (allowedTagList != null) {
                Collection<ResourceLocation> tagsOfItem = BuiltInRegistries.ITEM.getResourceKey(input.getItem())
                    .flatMap(k -> BuiltInRegistries.ITEM.getHolder(k)
                        .map(holder -> holder.tags()
                            .map(TagKey::location)
                            .collect(Collectors.toSet())))
                    .orElse(Collections.emptySet());
                Set<ResourceLocation> declaredAllowedTags = allowedTagList.getAllowedItemTags().get(i);

                for (ResourceLocation declaredAllowedTag : declaredAllowedTags) {
                    if (!tagsOfItem.contains(declaredAllowedTag)) {
                        throw new CraftingPatternFactoryException(
                            Component.translatable(
                                "misc.refinedstorage.pattern.error.tag_no_longer_applicable",
                                declaredAllowedTag.toString(),
                                input.getHoverName()
                            )
                        );
                    } else {
                        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, declaredAllowedTag);
                        for (Holder<Item> element : BuiltInRegistries.ITEM.getTagOrEmpty(tagKey)) {
                            possibilities.add(new ItemStack(element, input.getCount()));
                        }
                    }
                }
            }

            inputs.add(possibilities);
        }

        ItemStack output = PatternItem.getOutputSlot(stack, i);
        if (!output.isEmpty()) {
            outputs.add(output);
        }
    }

    private void fillProcessingFluidInputs(int i, ItemStack stack, List<NonNullList<FluidStack>> fluidInputs,
                                           NonNullList<FluidStack> fluidOutputs,
                                           @Nullable AllowedTagList allowedTagList)
        throws CraftingPatternFactoryException {
        FluidStack input = PatternItem.getFluidInputSlot(stack, i);
        if (input.isEmpty()) {
            fluidInputs.add(NonNullList.create());
        } else {
            NonNullList<FluidStack> possibilities = NonNullList.create();

            possibilities.add(input.copy());

            if (allowedTagList != null) {
                Collection<ResourceLocation> tagsOfFluid = BuiltInRegistries.FLUID.getResourceKey(input.getFluid())
                    .flatMap(k -> BuiltInRegistries.FLUID.getHolder(k)
                        .map(holder -> holder.tags()
                            .map(TagKey::location)
                            .collect(Collectors.toSet())))
                    .orElse(Collections.emptySet());
                Set<ResourceLocation> declaredAllowedTags = allowedTagList.getAllowedFluidTags().get(i);

                for (ResourceLocation declaredAllowedTag : declaredAllowedTags) {
                    if (!tagsOfFluid.contains(declaredAllowedTag)) {
                        throw new CraftingPatternFactoryException(
                            Component.translatable(
                                "misc.refinedstorage.pattern.error.tag_no_longer_applicable",
                                declaredAllowedTag.toString(),
                                input.getDisplayName()
                            )
                        );
                    } else {
                        TagKey<Fluid> tagKey =
                            TagKey.create(Registries.FLUID, declaredAllowedTag);
                        for (Holder<Fluid> element : BuiltInRegistries.FLUID.getTagOrEmpty(tagKey)) {
                            possibilities.add(new FluidStack(element, input.getAmount()));
                        }
                    }
                }
            }

            fluidInputs.add(possibilities);
        }

        FluidStack output = PatternItem.getFluidOutputSlot(stack, i);
        if (!output.isEmpty()) {
            fluidOutputs.add(output);
        }
    }

    private void fillCraftingInputs(CraftingContainer inv, ItemStack stack, List<NonNullList<ItemStack>> inputs,
                                    int i) {
        ItemStack input = PatternItem.getInputSlot(stack, i);

        inputs.add(input.isEmpty() ? NonNullList.create() : NonNullList.of(ItemStack.EMPTY, input));

        inv.setItem(i, input);
    }

    private void modifyCraftingInputsToUseAlternatives(CraftingRecipe recipe, List<NonNullList<ItemStack>> inputs) {
        if (!recipe.getIngredients().isEmpty()) {
            inputs.clear();

            for (int i = 0; i < recipe.getIngredients().size(); ++i) {
                inputs.add(i, NonNullList.of(ItemStack.EMPTY, recipe.getIngredients().get(i).getItems()));
            }
        }
    }
}

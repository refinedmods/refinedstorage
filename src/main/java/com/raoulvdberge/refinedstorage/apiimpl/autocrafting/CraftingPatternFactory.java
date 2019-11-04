package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.AllowedTags;
import com.raoulvdberge.refinedstorage.item.PatternItem;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class CraftingPatternFactory {
    public static final CraftingPatternFactory INSTANCE = new CraftingPatternFactory();

    public CraftingPattern create(World world, ICraftingPatternContainer container, ItemStack stack) {
        boolean processing = PatternItem.isProcessing(stack);
        boolean exact = PatternItem.isExact(stack);
        AllowedTags allowedTags = PatternItem.getAllowedTags(stack);

        List<NonNullList<ItemStack>> inputs = new ArrayList<>();
        NonNullList<ItemStack> outputs = NonNullList.create();
        NonNullList<ItemStack> byproducts = NonNullList.create();
        List<NonNullList<FluidStack>> fluidInputs = new ArrayList<>();
        NonNullList<FluidStack> fluidOutputs = NonNullList.create();
        ICraftingRecipe recipe = null;
        boolean valid = true;
        String errorMessage = null;

        try {
            if (processing) {
                for (int i = 0; i < 9; ++i) {
                    fillProcessingInputs(i, stack, inputs, outputs, allowedTags);
                    fillProcessingFluidInputs(i, stack, fluidInputs, fluidOutputs, allowedTags);
                }

                if (outputs.isEmpty() && fluidOutputs.isEmpty()) {
                    throw new Exception("Processing pattern has no outputs");
                }
            } else {
                CraftingInventory inv = new CraftingPattern.DummyCraftingInventory();

                for (int i = 0; i < 9; ++i) {
                    fillCraftingInputs(inv, stack, inputs, i);
                }

                Optional<ICraftingRecipe> foundRecipe = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world);
                if (foundRecipe.isPresent()) {
                    recipe = foundRecipe.get();

                    byproducts = recipe.getRemainingItems(inv);

                    ItemStack output = recipe.getCraftingResult(inv);

                    if (!output.isEmpty()) {
                        outputs.add(output);

                        if (!exact) {
                            modifyCraftingInputsToUseAlternatives(recipe, inputs);
                        }
                    } else {
                        throw new Exception("Recipe has no output");
                    }
                } else {
                    throw new Exception("Recipe doesn't exist");
                }
            }
        } catch (Exception e) {
            valid = false;
            errorMessage = e.getMessage();
        }

        return new CraftingPattern(container, stack, processing, exact, errorMessage, valid, recipe, inputs, outputs, byproducts, fluidInputs, fluidOutputs, allowedTags);
    }

    private void fillProcessingInputs(int i, ItemStack stack, List<NonNullList<ItemStack>> inputs, NonNullList<ItemStack> outputs, @Nullable AllowedTags allowedTags) throws Exception {
        ItemStack input = PatternItem.getInputSlot(stack, i);

        if (input.isEmpty()) {
            inputs.add(NonNullList.create());
        } else {
            NonNullList<ItemStack> possibilities = NonNullList.create();

            possibilities.add(input.copy());

            if (allowedTags != null) {
                Collection<ResourceLocation> tagsOfItem = ItemTags.getCollection().getOwningTags(input.getItem());
                Set<ResourceLocation> declaredAllowedTags = allowedTags.getAllowedItemTags().get(i);

                for (ResourceLocation declaredAllowedTag : declaredAllowedTags) {
                    if (!tagsOfItem.contains(declaredAllowedTag)) {
                        throw new Exception("Tag " + declaredAllowedTag.toString() + " is no longer applicable for " + input.getItem().getRegistryName().toString());
                    } else {
                        for (Item element : ItemTags.getCollection().get(declaredAllowedTag).getAllElements()) {
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

    private void fillProcessingFluidInputs(int i, ItemStack stack, List<NonNullList<FluidStack>> fluidInputs, NonNullList<FluidStack> fluidOutputs, @Nullable AllowedTags allowedTags) throws Exception {
        FluidStack input = PatternItem.getFluidInputSlot(stack, i);
        if (input.isEmpty()) {
            fluidInputs.add(NonNullList.create());
        } else {
            NonNullList<FluidStack> possibilities = NonNullList.create();

            possibilities.add(input.copy());

            if (allowedTags != null) {
                Collection<ResourceLocation> tagsOfFluid = FluidTags.getCollection().getOwningTags(input.getFluid());
                Set<ResourceLocation> declaredAllowedTags = allowedTags.getAllowedFluidTags().get(i);

                for (ResourceLocation declaredAllowedTag : declaredAllowedTags) {
                    if (!tagsOfFluid.contains(declaredAllowedTag)) {
                        throw new Exception("Tag " + declaredAllowedTag.toString() + " is no longer applicable for " + input.getFluid().getRegistryName().toString());
                    } else {
                        for (Fluid element : FluidTags.getCollection().get(declaredAllowedTag).getAllElements()) {
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

    private void fillCraftingInputs(CraftingInventory inv, ItemStack stack, List<NonNullList<ItemStack>> inputs, int i) {
        ItemStack input = PatternItem.getInputSlot(stack, i);

        inputs.add(input.isEmpty() ? NonNullList.create() : NonNullList.from(ItemStack.EMPTY, input));

        inv.setInventorySlotContents(i, input);
    }

    private void modifyCraftingInputsToUseAlternatives(ICraftingRecipe recipe, List<NonNullList<ItemStack>> inputs) throws Exception {
        if (!recipe.getIngredients().isEmpty()) {
            inputs.clear();

            for (int i = 0; i < recipe.getIngredients().size(); ++i) {
                inputs.add(i, NonNullList.from(ItemStack.EMPTY, recipe.getIngredients().get(i).getMatchingStacks()));
            }
        } else {
            throw new Exception("Recipe has no ingredients");
        }
    }
}

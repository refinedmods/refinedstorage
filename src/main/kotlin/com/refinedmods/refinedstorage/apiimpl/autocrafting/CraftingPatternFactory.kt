package com.refinedmods.refinedstorage.apiimpl.autocrafting

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern.DummyCraftingInventory
import com.refinedmods.refinedstorage.item.PatternItem
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ICraftingRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.tags.FluidTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.NonNullList
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidInstance
import java.util.*


class CraftingPatternFactory {
    fun create(world: World, container: ICraftingPatternContainer, stack: ItemStack): CraftingPattern {
        val processing = PatternItem.isProcessing(stack)
        val exact = PatternItem.isExact(stack)
        val allowedTagList = PatternItem.getAllowedTags(stack)
        val inputs: MutableList<NonNullList<ItemStack>> = ArrayList<NonNullList<ItemStack>>()
        val outputs: NonNullList<ItemStack> = NonNullList.create()
        var byproducts: NonNullList<ItemStack?> = NonNullList.create()
        val fluidInputs: MutableList<NonNullList<FluidInstance>> = ArrayList<NonNullList<FluidInstance>>()
        val fluidOutputs: NonNullList<FluidInstance> = NonNullList.create()
        var recipe: ICraftingRecipe? = null
        var valid = true
        var errorMessage: Text? = null
        try {
            if (processing) {
                for (i in 0..8) {
                    fillProcessingInputs(i, stack, inputs, outputs, allowedTagList)
                    fillProcessingFluidInputs(i, stack, fluidInputs, fluidOutputs, allowedTagList)
                }
                if (outputs.isEmpty() && fluidOutputs.isEmpty()) {
                    throw CraftingPatternFactoryException(TranslationTextComponent("misc.refinedstorage.pattern.error.processing_no_outputs"))
                }
            } else {
                val inv: CraftingInventory = DummyCraftingInventory()
                for (i in 0..8) {
                    fillCraftingInputs(inv, stack, inputs, i)
                }
                val foundRecipe: Optional<ICraftingRecipe> = world.recipeManager.getRecipe(IRecipeType.CRAFTING, inv, world)
                if (foundRecipe.isPresent()) {
                    recipe = foundRecipe.get()
                    byproducts = recipe.getRemainingItems(inv)
                    val output: ItemStack = recipe.getCraftingResult(inv)
                    if (!output.isEmpty) {
                        outputs.add(output)
                        if (!exact) {
                            modifyCraftingInputsToUseAlternatives(recipe, inputs)
                        }
                    } else {
                        throw CraftingPatternFactoryException(TranslationTextComponent("misc.refinedstorage.pattern.error.no_output"))
                    }
                } else {
                    throw CraftingPatternFactoryException(TranslationTextComponent("misc.refinedstorage.pattern.error.recipe_does_not_exist"))
                }
            }
        } catch (e: CraftingPatternFactoryException) {
            valid = false
            errorMessage = e.errorMessage
        }
        return CraftingPattern(container, stack, processing, exact, errorMessage, valid, recipe, inputs, outputs, byproducts, fluidInputs, fluidOutputs, allowedTagList)
    }

    @Throws(CraftingPatternFactoryException::class)
    private fun fillProcessingInputs(i: Int, stack: ItemStack, inputs: MutableList<NonNullList<ItemStack>>, outputs: NonNullList<ItemStack>, @Nullable allowedTagList: AllowedTagList?) {
        val input = PatternItem.getInputSlot(stack, i)
        if (input.isEmpty) {
            inputs.add(NonNullList.create())
        } else {
            val possibilities: NonNullList<ItemStack> = NonNullList.create()
            possibilities.add(input.copy())
            if (allowedTagList != null) {
                val tagsOfItem: Collection<Identifier> = ItemTags.getCollection().getOwningTags(input.item)
                val declaredAllowedTags: Set<Identifier?>? = allowedTagList.allowedItemTags[i]
                for (declaredAllowedTag in declaredAllowedTags!!) {
                    if (!tagsOfItem.contains(declaredAllowedTag)) {
                        throw CraftingPatternFactoryException(
                                TranslationTextComponent(
                                        "misc.refinedstorage.pattern.error.tag_no_longer_applicable",
                                        declaredAllowedTag.toString(),
                                        input.getDisplayName()
                                )
                        )
                    } else {
                        for (element in ItemTags.getCollection().get(declaredAllowedTag).getAllElements()) {
                            possibilities.add(ItemStack(element, input.count))
                        }
                    }
                }
            }
            inputs.add(possibilities)
        }
        val output = PatternItem.getOutputSlot(stack, i)
        if (!output.isEmpty) {
            outputs.add(output)
        }
    }

    @Throws(CraftingPatternFactoryException::class)
    private fun fillProcessingFluidInputs(i: Int, stack: ItemStack, fluidInputs: MutableList<NonNullList<FluidInstance>>, fluidOutputs: NonNullList<FluidInstance>, @Nullable allowedTagList: AllowedTagList?) {
        val input: FluidInstance = PatternItem.getFluidInputSlot(stack, i)
        if (input.isEmpty()) {
            fluidInputs.add(NonNullList.create())
        } else {
            val possibilities: NonNullList<FluidInstance> = NonNullList.create()
            possibilities.add(input.copy())
            if (allowedTagList != null) {
                val tagsOfFluid: Collection<Identifier> = FluidTags.getCollection().getOwningTags(input.getFluid())
                val declaredAllowedTags: Set<Identifier?>? = allowedTagList.allowedFluidTags[i]
                for (declaredAllowedTag in declaredAllowedTags!!) {
                    if (!tagsOfFluid.contains(declaredAllowedTag)) {
                        throw CraftingPatternFactoryException(
                                TranslationTextComponent(
                                        "misc.refinedstorage.pattern.error.tag_no_longer_applicable",
                                        declaredAllowedTag.toString(),
                                        input.getDisplayName()
                                )
                        )
                    } else {
                        for (element in FluidTags.getCollection().get(declaredAllowedTag).getAllElements()) {
                            possibilities.add(FluidInstance(element, input.getAmount()))
                        }
                    }
                }
            }
            fluidInputs.add(possibilities)
        }
        val output: FluidInstance = PatternItem.getFluidOutputSlot(stack, i)
        if (!output.isEmpty()) {
            fluidOutputs.add(output)
        }
    }

    private fun fillCraftingInputs(inv: CraftingInventory, stack: ItemStack, inputs: MutableList<NonNullList<ItemStack>>, i: Int) {
        val input = PatternItem.getInputSlot(stack, i)
        inputs.add(if (input.isEmpty) NonNullList.create() else NonNullList.from(ItemStack.EMPTY, input))
        inv.setInventorySlotContents(i, input)
    }

    private fun modifyCraftingInputsToUseAlternatives(recipe: ICraftingRecipe?, inputs: MutableList<NonNullList<ItemStack>>) {
        if (!recipe.getIngredients().isEmpty()) {
            inputs.clear()
            for (i in 0 until recipe.getIngredients().size()) {
                inputs.add(i, NonNullList.from(ItemStack.EMPTY, recipe.getIngredients().get(i).getMatchingStacks()))
            }
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        val INSTANCE = CraftingPatternFactory()
    }
}
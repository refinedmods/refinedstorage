package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fluids.FluidInstanceimport
import java.util.*

class CraftingPatternInputs(pattern: ICraftingPattern) {
    private val recipe: NonNullList<ItemStack> = NonNullList.create()
    private val itemIngredients: MutableList<Ingredient<ItemStack>> = ArrayList<Ingredient<ItemStack>>()
    private val fluidIngredients: MutableList<Ingredient<FluidInstance>> = ArrayList<Ingredient<FluidInstance>>()
    private fun fillOutRecipe(pattern: ICraftingPattern) {
        for (inputsForSlot in pattern.getInputs()) {
            if (inputsForSlot.isEmpty()) {
                recipe.add(ItemStack.EMPTY)
            } else {
                recipe.add(inputsForSlot.get(0))
            }
        }
    }

    private fun combineItemInputs(pattern: ICraftingPattern) {
        for (inputsForSlot in pattern.getInputs()) {
            if (inputsForSlot.isEmpty()) {
                continue
            }
            val matchingIngredient: Ingredient<ItemStack>? = findMatchingItemIngredient(inputsForSlot)
            matchingIngredient?.increaseCount(inputsForSlot.get(0).getCount())
                    ?: itemIngredients.add(Ingredient<T>(inputsForSlot, inputsForSlot.get(0).getCount()))
        }
    }

    private fun combineFluidInputs(pattern: ICraftingPattern) {
        for (inputsForSlot in pattern.getFluidInputs()) {
            if (inputsForSlot.isEmpty()) {
                continue
            }
            val matchingIngredient: Ingredient<FluidInstance>? = findMatchingFluidIngredient(inputsForSlot)
            matchingIngredient?.increaseCount(inputsForSlot.get(0).getAmount())
                    ?: fluidIngredients.add(Ingredient<T>(inputsForSlot, inputsForSlot.get(0).getAmount()))
        }
    }

    @Nullable
    private fun findMatchingItemIngredient(inputsForSlot: NonNullList<ItemStack>): Ingredient<ItemStack>? {
        for (existingIngredient in itemIngredients) {
            if (existingIngredient.getInputs().size() === inputsForSlot.size()) {
                var found = true
                for (i in 0 until inputsForSlot.size()) {
                    if (!API.instance().getComparer().isEqualNoQuantity(existingIngredient.getInputs().get(i), inputsForSlot.get(i))) {
                        found = false
                        break
                    }
                }
                if (found) {
                    return existingIngredient
                }
            }
        }
        return null
    }

    @Nullable
    private fun findMatchingFluidIngredient(inputsForSlot: NonNullList<FluidInstance>): Ingredient<FluidInstance>? {
        for (existingIngredient in fluidIngredients) {
            if (existingIngredient.getInputs().size() === inputsForSlot.size()) {
                var found = true
                for (i in 0 until inputsForSlot.size()) {
                    if (!API.instance().getComparer().isEqual(existingIngredient.getInputs().get(i), inputsForSlot.get(i), IComparer.COMPARE_NBT)) {
                        found = false
                        break
                    }
                }
                if (found) {
                    return existingIngredient
                }
            }
        }
        return null
    }

    fun getRecipe(): NonNullList<ItemStack> {
        return recipe
    }

    fun getItemIngredients(): List<Ingredient<ItemStack>> {
        return itemIngredients
    }

    fun getFluidIngredients(): List<Ingredient<FluidInstance>> {
        return fluidIngredients
    }

    class Ingredient<T>(inputs: NonNullList<T>, count: Int) {
        private val inputs: NonNullList<T>
        var count: Int
            private set

        fun getInputs(): NonNullList<T> {
            return inputs
        }

        fun increaseCount(count: Int) {
            this.count += count
        }

        init {
            this.inputs = inputs
            this.count = count
        }
    }

    init {
        fillOutRecipe(pattern)
        combineItemInputs(pattern)
        combineFluidInputs(pattern)
    }
}
package com.refinedmods.refinedstorage.apiimpl.autocrafting

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTaskFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.container.Container
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.ICraftingRecipe
import net.minecraft.util.NonNullList
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraftforge.fluids.FluidInstance


class CraftingPattern(private val container: ICraftingPatternContainer, private val stack: ItemStack, private val processing: Boolean, private val exact: Boolean, @Nullable errorMessage: Text?, private val valid: Boolean, @Nullable recipe: ICraftingRecipe?, inputs: List<NonNullList<ItemStack>>, outputs: NonNullList<ItemStack>, byproducts: NonNullList<ItemStack?>, fluidInputs: List<NonNullList<FluidInstance>>, fluidOutputs: NonNullList<FluidInstance>, @Nullable allowedTagList: AllowedTagList) : ICraftingPattern {
    @Nullable
    private val errorMessage: Text?

    @Nullable
    private val recipe: ICraftingRecipe?
    private val inputs: List<NonNullList<ItemStack>>
    private val outputs: NonNullList<ItemStack>
    private val byproducts: NonNullList<ItemStack>
    private val fluidInputs: List<NonNullList<FluidInstance>>
    private val fluidOutputs: NonNullList<FluidInstance>

    @get:Nullable
    @Nullable
    val allowedTagList: AllowedTagList
    override fun getContainer(): ICraftingPatternContainer? {
        return container
    }

    override fun getStack(): ItemStack? {
        return stack
    }

    override fun isValid(): Boolean {
        return valid
    }

    @Nullable
    override fun getErrorMessage(): Text? {
        return errorMessage
    }

    override fun isProcessing(): Boolean {
        return processing
    }

    override fun getInputs(): List<NonNullList<ItemStack>> {
        return inputs
    }

    override fun getOutputs(): NonNullList<ItemStack> {
        return outputs
    }

    override fun getOutput(took: NonNullList<ItemStack?>): ItemStack? {
        check(!processing) { "Cannot get crafting output from processing pattern" }
        require(!(took.size() !== inputs.size)) { "The items that are taken (" + took.size().toString() + ") should match the inputs for this pattern (" + inputs.size.toString() + ")" }
        val inv: CraftingInventory = DummyCraftingInventory()
        for (i in 0 until took.size()) {
            inv.setInventorySlotContents(i, took.get(i))
        }
        val result: ItemStack = recipe.getCraftingResult(inv)
        check(!result.isEmpty) { "Cannot have empty result" }
        return result
    }

    override fun getByproducts(): NonNullList<ItemStack> {
        check(!processing) { "Cannot get byproduct outputs from processing pattern" }
        return byproducts
    }

    override fun getByproducts(took: NonNullList<ItemStack?>): NonNullList<ItemStack> {
        check(!processing) { "Cannot get byproduct outputs from processing pattern" }
        require(!(took.size() !== inputs.size)) { "The items that are taken (" + took.size().toString() + ") should match the inputs for this pattern (" + inputs.size.toString() + ")" }
        val inv: CraftingInventory = DummyCraftingInventory()
        for (i in 0 until took.size()) {
            inv.setInventorySlotContents(i, took.get(i))
        }
        val remainingItems: NonNullList<ItemStack> = recipe.getRemainingItems(inv)
        val sanitized: NonNullList<ItemStack> = NonNullList.create()
        for (item in remainingItems) {
            if (!item.isEmpty) {
                sanitized.add(item)
            }
        }
        return sanitized
    }

    override fun getFluidInputs(): List<NonNullList<FluidInstance>> {
        return fluidInputs
    }

    override fun getFluidOutputs(): NonNullList<FluidInstance> {
        return fluidOutputs
    }

    override fun getCraftingTaskFactoryId(): Identifier? {
        return CraftingTaskFactory.Companion.ID
    }

    override fun equals(otherObj: Any?): Boolean {
        if (otherObj !is ICraftingPattern) {
            return false
        }
        val other = otherObj
        if (other.isProcessing() != processing) {
            return false
        }
        if (other.getInputs()!!.size != inputs.size ||
                other.getFluidInputs()!!.size != fluidInputs.size ||
                other.getOutputs().size() !== outputs.size() ||
                other.getFluidOutputs().size() !== fluidOutputs.size()) {
            return false
        }
        if (!processing && other.getByproducts().size() !== byproducts.size()) {
            return false
        }
        for (i in inputs.indices) {
            val inputs: List<ItemStack> = inputs[i]
            val otherInputs: List<ItemStack> = other.getInputs()!![i]
            if (inputs.size != otherInputs.size) {
                return false
            }
            for (j in inputs.indices) {
                if (!instance().getComparer()!!.isEqual(inputs[j], otherInputs[j])) {
                    return false
                }
            }
        }
        for (i in fluidInputs.indices) {
            val inputs: List<FluidInstance> = fluidInputs[i]
            val otherInputs: List<FluidInstance> = other.getFluidInputs()!![i]
            if (inputs.size != otherInputs.size) {
                return false
            }
            for (j in inputs.indices) {
                if (!instance().getComparer().isEqual(inputs[j], otherInputs[j], IComparer.COMPARE_NBT or IComparer.COMPARE_QUANTITY)) {
                    return false
                }
            }
        }
        for (i in 0 until outputs.size()) {
            if (!instance().getComparer()!!.isEqual(outputs.get(i), other.getOutputs().get(i))) {
                return false
            }
        }
        for (i in 0 until fluidOutputs.size()) {
            if (!instance().getComparer().isEqual(fluidOutputs.get(i), other.getFluidOutputs().get(i), IComparer.COMPARE_NBT or IComparer.COMPARE_QUANTITY)) {
                return false
            }
        }
        if (!processing) {
            for (i in 0 until byproducts.size()) {
                if (!instance().getComparer()!!.isEqual(byproducts.get(i), other.getByproducts().get(i))) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + if (processing) 1 else 0
        result = 31 * result + if (exact) 1 else 0
        for (inputs in inputs) {
            for (input in inputs) {
                result = 31 * result + instance().getItemStackHashCode(input)
            }
        }
        for (inputs in fluidInputs) {
            for (input in inputs) {
                result = 31 * result + instance().getFluidInstanceHashCode(input)
            }
        }
        for (output in outputs) {
            result = 31 * result + instance().getItemStackHashCode(output)
        }
        for (output in fluidOutputs) {
            result = 31 * result + instance().getFluidInstanceHashCode(output)
        }
        for (byproduct in byproducts) {
            result = 31 * result + instance().getItemStackHashCode(byproduct)
        }
        return result
    }

    class DummyCraftingInventory : CraftingInventory(object : Container(null, 0) {
        fun canInteractWith(player: PlayerEntity?): Boolean {
            return true
        }
    }, 3, 3)

    init {
        this.errorMessage = errorMessage
        this.recipe = recipe
        this.inputs = inputs
        this.outputs = outputs
        this.byproducts = byproducts
        this.fluidInputs = fluidInputs
        this.fluidOutputs = fluidOutputs
        this.allowedTagList = allowedTagList
    }
}
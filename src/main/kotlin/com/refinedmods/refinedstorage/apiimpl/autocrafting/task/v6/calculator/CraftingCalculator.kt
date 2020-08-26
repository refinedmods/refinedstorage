package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingPatternInputs
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTask
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculatorimport.PossibleInputs
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingNode
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeList
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingNode
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview.CraftingPreviewElementFactory
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview.CraftingPreviewInfo
import com.refinedmods.refinedstorage.util.StackUtils
import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance

class CraftingCalculator(
        private val network: INetwork,
        private val requested: ICraftingRequestInfo,
        private val quantity: Int,
        private val pattern: ICraftingPattern
) {
    private val patternsUsed: MutableSet<ICraftingPattern> = HashSet()
    private val craftingPreviewInfo: CraftingPreviewInfo = CraftingPreviewInfo()
    private val nodes = NodeList()
    private val toExtractInitial: IStackList<ItemStack> = API.instance().createItemStackList()
    private val toExtractInitialFluids: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
    private var calculationStarted: Long = -1
    fun calculate(): ICalculationResult {
        calculationStarted = System.currentTimeMillis()
        val results: IStackList<ItemStack> = API.instance().createItemStackList()
        val fluidResults: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
        val storageSource: IStackList<ItemStack> = network.itemStorageCache.getList().copy()
        val fluidStorageSource: IStackList<FluidInstance> = network.fluidStorageCache.getList().copy()
        val qtyPerCraft = getQuantityPerCraft(requested.item, requested.fluid, pattern)
        val qty = (quantity - 1) / qtyPerCraft + 1

        try {
            calculateInternal(qty, storageSource, fluidStorageSource, results, fluidResults, pattern, true)
        } catch (e: CraftingCalculatorException) {
            return CalculationResult(e.type, e.recursedPattern)
        }

        if (requested.item != null) {
            craftingPreviewInfo.getToCraft().add(ItemHandlerHelper.copyStackWithSize(requested.item, qty * qtyPerCraft))
        } else if (requested.fluid != null) {
            craftingPreviewInfo.getToCraftFluids().add(StackUtils.copy(requested.fluid, qty * qtyPerCraft))
        }

        val previewElements: List<ICraftingPreviewElement<*>> = CraftingPreviewElementFactory().getElements(craftingPreviewInfo)
        return if (craftingPreviewInfo.hasMissing()) {
            CalculationResult(CalculationResultType.MISSING, previewElements, null)
        } else CalculationResult(
                CalculationResultType.OK,
                previewElements,
                CraftingTask(network, requested, quantity, pattern, nodes, toExtractInitial, toExtractInitialFluids)
        )
    }

    @Throws(CraftingCalculatorException::class)
    private fun calculateInternal(
            qty: Int,
            storageSource: IStackList<ItemStack>,
            fluidStorageSource: IStackList<FluidInstance>,
            results: IStackList<ItemStack>,
            fluidResults: IStackList<FluidInstance>,
            pattern: ICraftingPattern,
            root: Boolean) {
        if (System.currentTimeMillis() - calculationStarted > RS.SERVER_CONFIG.getAutocrafting().getCalculationTimeoutMs()) {
            throw CraftingCalculatorException(CalculationResultType.TOO_COMPLEX, null)
        }
        if (!patternsUsed.add(pattern)) {
            throw CraftingCalculatorException(CalculationResultType.RECURSIVE, pattern)
        }
        val itemsToExtract: IStackList<ItemStack> = API.instance().createItemStackList()
        val fluidsToExtract: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
        val inputs = CraftingPatternInputs(pattern)
        val node = nodes.createOrAddToExistingNode(pattern, root, inputs.getRecipe(), qty)
        calculateForItems(qty, storageSource, fluidStorageSource, results, fluidResults, itemsToExtract, inputs, node)
        if (node is CraftingNode) {
            val output: ItemStack = pattern.getOutput(inputs.getRecipe())
            results.add(output, output.getCount() * qty)
            for (byproduct in pattern.getByproducts(inputs.getRecipe())) {
                results.add(byproduct, byproduct.getCount() * qty)
            }
        } else if (node is ProcessingNode) {
            val processing: ProcessingNode = node as ProcessingNode
            calculateForFluids(qty, storageSource, fluidStorageSource, results, fluidResults, pattern, inputs, fluidsToExtract, processing)
            for (output in pattern.getOutputs()) {
                results.add(output, output.getCount() * qty)
            }
            for (output in pattern.getFluidOutputs()) {
                fluidResults.add(output, output.getAmount() * qty)
            }
        }
        patternsUsed.remove(pattern)
    }

    @Throws(CraftingCalculatorException::class)
    private fun calculateForItems(qty: Int,
                                  storageSource: IStackList<ItemStack>,
                                  fluidStorageSource: IStackList<FluidInstance>,
                                  results: IStackList<ItemStack>,
                                  fluidResults: IStackList<FluidInstance>,
                                  itemsToExtract: IStackList<ItemStack>,
                                  inputs: CraftingPatternInputs,
                                  node: Node?) {
        var ingredientNumber = -1
        for (ingredient in inputs.getItemIngredients()) {
            ingredientNumber++
            val possibleInputs: PossibleInputs<ItemStack> = PossibleInputs<ItemStack>(ingredient.getInputs())
            possibleInputs.sort(storageSource, results)
            var possibleInput: ItemStack? = possibleInputs.get()
            var fromSelf: ItemStack = results.get(possibleInput)
            var fromNetwork: ItemStack = storageSource.get(possibleInput)
            var remaining = ingredient.getCount() * qty
            if (remaining < 0) { // int overflow
                throw CraftingCalculatorException(CalculationResultType.TOO_COMPLEX)
            }
            while (remaining > 0) {
                if (fromSelf != null) {
                    val toTake = Math.min(remaining, fromSelf.getCount())
                    node.getRequirements().addItemRequirement(ingredientNumber, possibleInput, toTake, ingredient.getCount())
                    results.remove(fromSelf, toTake)
                    remaining -= toTake
                    fromSelf = results.get(possibleInput)
                }
                if (fromNetwork != null && remaining > 0) {
                    val toTake = Math.min(remaining, fromNetwork.getCount())
                    craftingPreviewInfo.getToTake().add(possibleInput, toTake)
                    node.getRequirements().addItemRequirement(ingredientNumber, possibleInput, toTake, ingredient.getCount())
                    storageSource.remove(fromNetwork, toTake)
                    remaining -= toTake
                    fromNetwork = storageSource.get(possibleInput)
                    toExtractInitial.add(possibleInput, toTake)
                }
                if (remaining > 0) {
                    val subPattern: ICraftingPattern = network.craftingManager.getPattern(possibleInput)
                    if (subPattern != null) {
                        val qtyPerCraft = getQuantityPerCraft(possibleInput, null, subPattern)
                        val subQty = (remaining - 1) / qtyPerCraft + 1
                        calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false)
                        fromSelf = results.get(possibleInput)
                        checkNotNull(fromSelf) { "Recursive calculation didn't yield anything" }
                        fromNetwork = storageSource.get(possibleInput)

                        // fromSelf contains the amount crafted after the loop.
                        craftingPreviewInfo.getToCraft().add(fromSelf.copy())
                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = possibleInputs.get() // Revert back to 0.
                            craftingPreviewInfo.getMissing().add(possibleInput, remaining)
                            itemsToExtract.add(possibleInput, remaining)
                            remaining = 0
                        } else {
                            // Retry with new input...
                            possibleInput = possibleInputs.get()
                            fromSelf = results.get(possibleInput)
                            fromNetwork = storageSource.get(possibleInput)
                        }
                    }
                }
            }
        }
    }

    @Throws(CraftingCalculatorException::class)
    private fun calculateForFluids(qty: Int,
                                   storageSource: IStackList<ItemStack>,
                                   fluidStorageSource: IStackList<FluidInstance>,
                                   results: IStackList<ItemStack>,
                                   fluidResults: IStackList<FluidInstance>,
                                   pattern: ICraftingPattern,
                                   inputs: CraftingPatternInputs,
                                   fluidsToExtract: IStackList<FluidInstance>,
                                   node: ProcessingNode?) {
        var ingredientNumber = -1
        for (ingredient in inputs.getFluidIngredients()) {
            ingredientNumber++
            val possibleInputs: PossibleInputs<FluidInstance> = PossibleInputs<FluidInstance>(ingredient.getInputs())
            possibleInputs.sort(fluidStorageSource, fluidResults)
            var possibleInput: FluidInstance = possibleInputs.get()
            var fromSelf: FluidInstance = fluidResults.get(possibleInput, IComparer.COMPARE_NBT)
            var fromNetwork: FluidInstance = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT)
            var remaining: Int = possibleInput.getAmount() * qty
            if (remaining < 0) { // int overflow
                throw CraftingCalculatorException(CalculationResultType.TOO_COMPLEX)
            }
            while (remaining > 0) {
                if (fromSelf != null) {
                    val toTake = Math.min(remaining, fromSelf.getAmount())
                    node.getRequirements().addFluidRequirement(ingredientNumber, possibleInput, toTake, ingredient.getCount())
                    fluidResults.remove(possibleInput, toTake)
                    remaining -= toTake
                    fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT)
                }
                if (fromNetwork != null && remaining > 0) {
                    val toTake = Math.min(remaining, fromNetwork.getAmount())
                    node.getRequirements().addFluidRequirement(ingredientNumber, possibleInput, toTake, ingredient.getCount())
                    craftingPreviewInfo.getToTakeFluids().add(possibleInput, toTake)
                    fluidStorageSource.remove(fromNetwork, toTake)
                    remaining -= toTake
                    fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT)
                    toExtractInitialFluids.add(possibleInput, toTake)
                }
                if (remaining > 0) {
                    val subPattern: ICraftingPattern = network.craftingManager.getPattern(possibleInput)
                    if (subPattern != null) {
                        val qtyPerCraft = getQuantityPerCraft(null, possibleInput, subPattern)
                        val subQty = (remaining - 1) / qtyPerCraft + 1
                        calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false)
                        fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT)
                        checkNotNull(fromSelf) { "Recursive fluid calculation didn't yield anything" }
                        fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT)

                        // fromSelf contains the amount crafted after the loop.
                        craftingPreviewInfo.getToCraftFluids().add(fromSelf.copy())
                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = possibleInputs.get() // Revert back to 0.
                            craftingPreviewInfo.getMissingFluids().add(possibleInput, remaining)
                            fluidsToExtract.add(possibleInput, remaining)
                            remaining = 0
                        } else {
                            // Retry with new input...
                            possibleInput = possibleInputs.get()
                            fromSelf = fluidResults.get(possibleInput)
                            fromNetwork = fluidStorageSource.get(possibleInput)
                        }
                    }
                }
            }
        }
    }

    private fun getQuantityPerCraft(@Nullable item: ItemStack?, @Nullable fluid: FluidInstance?, pattern: ICraftingPattern): Int {
        var qty = 0
        if (item != null) {
            for (output in pattern.getOutputs()) {
                if (API.instance().getComparer().isEqualNoQuantity(output, item)) {
                    qty += output.getCount()
                    if (!pattern.isProcessing()) {
                        break
                    }
                }
            }
        } else if (fluid != null) {
            for (output in pattern.getFluidOutputs()) {
                if (API.instance().getComparer().isEqual(output, fluid, IComparer.COMPARE_NBT)) {
                    qty += output.getAmount()
                }
            }
        }
        return qty
    }

}
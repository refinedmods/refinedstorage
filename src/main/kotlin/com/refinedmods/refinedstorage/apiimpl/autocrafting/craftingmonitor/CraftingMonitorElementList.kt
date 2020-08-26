package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList
import net.minecraft.util.Identifier

class CraftingMonitorElementList : ICraftingMonitorElementList {
    private val elements: MutableList<ICraftingMonitorElement> = mutableListOf()
    private val currentLists: MutableMap<Identifier, MutableMap<Int, ICraftingMonitorElement>> = LinkedHashMap()
    private val currentCraftingLists: MutableMap<Identifier, MutableMap<Int, ICraftingMonitorElement>> = LinkedHashMap()
    private val currentProcessingLists: MutableMap<Identifier, MutableMap<Int, ICraftingMonitorElement>> = LinkedHashMap()
    private val currentStorageLists: MutableMap<Identifier, MutableMap<Int, ICraftingMonitorElement>> = LinkedHashMap()
    override fun directAdd(element: ICraftingMonitorElement) {
        elements.add(element)
    }

    override fun addStorage(element: ICraftingMonitorElement) {
        val craftingElements: Map<Int, ICraftingMonitorElement>? = currentCraftingLists[element.getBaseId()]
        val processingElements: Map<Int, ICraftingMonitorElement>? = currentProcessingLists[element.getBaseId()]
        var storedElements: MutableMap<Int, ICraftingMonitorElement>? = currentStorageLists[element.getBaseId()]
        var merged = false

        craftingElements?.get(element.baseElementHashCode())?.let { existingElement ->
                when (existingElement) {
                    is ErrorCraftingMonitorElement -> existingElement.mergeBases(element)
                    else -> existingElement.merge(element)
                }
                merged = true
        }

        processingElements?.get(element.baseElementHashCode())?.let { existingElement ->
                when (existingElement) {
                    is ErrorCraftingMonitorElement -> existingElement.mergeBases(element)
                    else -> existingElement.merge(element)
                }
                merged = true
        }

        if (!merged) {
            if (storedElements == null) {
                storedElements = hashMapOf()
            }
            storedElements[element.baseElementHashCode()] = element
            currentStorageLists[element.getBaseId()] = storedElements
        }
    }

    override fun add(element: ICraftingMonitorElement, isProcessing: Boolean) {
        val currentElements = when {
            isProcessing -> currentProcessingLists[element.getBaseId()]
            else -> currentCraftingLists[element.getBaseId()]
        } ?: LinkedHashMap()

        var existingElement: ICraftingMonitorElement? = currentElements[element.baseElementHashCode()]
        when (existingElement) {
            null -> existingElement = element
            else -> existingElement.merge(element)
        }
        currentElements[existingElement.baseElementHashCode()] = existingElement
        when {
            isProcessing -> currentProcessingLists[existingElement.getBaseId()] = currentElements
            else -> currentCraftingLists[existingElement.getBaseId()] = currentElements
        }
    }

    override fun add(element: ICraftingMonitorElement) {
        val currentElements: MutableMap<Int, ICraftingMonitorElement> =
                currentLists[element.getId()] ?: hashMapOf()

        var exitingElement: ICraftingMonitorElement? = currentElements[element.elementHashCode()]
        when (exitingElement) {
            null -> exitingElement = element
            else -> exitingElement.merge(element)
        }
        currentElements[exitingElement.elementHashCode()] = exitingElement
        currentLists[exitingElement.getId()] = currentElements
    }

    override fun commit() {
        currentLists.values
                .map { obj -> obj.values }
                .flatten()
                .forEach { e -> elements.add(e) }
        currentLists.clear()

        currentCraftingLists.values
                .map { obj -> obj.values }
                .flatten()
                .forEach{ e -> elements.add(e) }
        currentCraftingLists.clear()

        currentProcessingLists.values
                .map { obj -> obj.values }
                .flatten()
                .forEach{ e -> elements.add(e) }
        currentProcessingLists.clear()

        currentStorageLists.values
                .map { obj -> obj.values }
                .flatten()
                .forEach{ e -> elements.add(e) }
        currentStorageLists.clear()
    }

    override fun getElements(): List<ICraftingMonitorElement> {
        if (currentLists.isNotEmpty() || currentCraftingLists.isNotEmpty() || currentProcessingLists.isNotEmpty() || currentStorageLists.isNotEmpty()) {
            commit()
        }
        return elements
    }
}
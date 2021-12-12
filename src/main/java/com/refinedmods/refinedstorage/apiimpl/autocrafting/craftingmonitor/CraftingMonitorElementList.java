package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class CraftingMonitorElementList implements ICraftingMonitorElementList {
    private final List<ICraftingMonitorElement> elements = new LinkedList<>();
    private final Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentLists = new LinkedHashMap<>();
    private final Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentCraftingLists = new LinkedHashMap<>();
    private final Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentProcessingLists = new LinkedHashMap<>();
    private final Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentStorageLists = new LinkedHashMap<>();

    @Override
    public void directAdd(ICraftingMonitorElement element) {
        elements.add(element);
    }

    @Override
    public void addStorage(ICraftingMonitorElement element) {
        Map<Integer, ICraftingMonitorElement> craftingElements = currentCraftingLists.get(element.getBaseId());
        Map<Integer, ICraftingMonitorElement> processingElements = currentProcessingLists.get(element.getBaseId());
        Map<Integer, ICraftingMonitorElement> storedElements = currentStorageLists.get(element.getBaseId());
        boolean merged = false;
        if (craftingElements != null) {
            ICraftingMonitorElement existingElement = craftingElements.get(element.baseElementHashCode());
            if (existingElement != null) {
                if (existingElement instanceof ErrorCraftingMonitorElement) {
                    ((ErrorCraftingMonitorElement) existingElement).mergeBases(element);
                } else {
                    existingElement.merge(element);
                }
                merged = true;
            }
        }
        if (processingElements != null) {
            ICraftingMonitorElement existingElement = processingElements.get(element.baseElementHashCode());
            if (existingElement != null) {
                if (existingElement instanceof ErrorCraftingMonitorElement) {
                    ((ErrorCraftingMonitorElement) existingElement).mergeBases(element);
                } else {
                    existingElement.merge(element);
                }
                merged = true;
            }
        }
        if (!merged) {
            if (storedElements == null) {
                storedElements = new HashMap<>();
            }
            storedElements.put(element.baseElementHashCode(), element);
            currentStorageLists.put(element.getBaseId(), storedElements);
        }
    }

    @Override
    public void add(ICraftingMonitorElement element, boolean isProcessing) {
        Map<Integer, ICraftingMonitorElement> currentElements = isProcessing ? currentProcessingLists.get(element.getBaseId()) : currentCraftingLists.get(element.getBaseId());

        if (currentElements == null) {
            currentElements = new LinkedHashMap<>();
        }

        ICraftingMonitorElement existingElement = currentElements.get(element.baseElementHashCode());

        if (existingElement == null) {
            existingElement = element;
        } else {
            existingElement.merge(element);
        }

        currentElements.put(existingElement.baseElementHashCode(), existingElement);
        if (isProcessing) {
            currentProcessingLists.put(existingElement.getBaseId(), currentElements);
        } else {
            currentCraftingLists.put(existingElement.getBaseId(), currentElements);
        }

    }

    @Override
    public void add(ICraftingMonitorElement element) {
        Map<Integer, ICraftingMonitorElement> currentElements = currentLists.get(element.getId());

        if (currentElements == null) {
            currentElements = new HashMap<>();
        }

        ICraftingMonitorElement exitingElement = currentElements.get(element.elementHashCode());

        if (exitingElement == null) {
            exitingElement = element;
        } else {
            exitingElement.merge(element);
        }

        currentElements.put(exitingElement.elementHashCode(), exitingElement);
        currentLists.put(exitingElement.getId(), currentElements);
    }

    @Override
    public void commit() {
        currentLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentLists.clear();
        currentCraftingLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentCraftingLists.clear();
        currentProcessingLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentProcessingLists.clear();
        currentStorageLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentStorageLists.clear();
    }

    @Override
    public List<ICraftingMonitorElement> getElements() {
        if (!currentLists.isEmpty() || !currentCraftingLists.isEmpty() || !currentProcessingLists.isEmpty() || !currentStorageLists.isEmpty()) {
            commit();
        }

        return elements;
    }
}

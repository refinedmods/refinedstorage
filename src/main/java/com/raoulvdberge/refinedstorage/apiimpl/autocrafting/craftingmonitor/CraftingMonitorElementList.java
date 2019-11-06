package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class CraftingMonitorElementList implements ICraftingMonitorElementList {
    private List<ICraftingMonitorElement> elements = new LinkedList<>();
    private Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentCraftingLists = new LinkedHashMap<>();
    private Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentProcessingLists = new LinkedHashMap<>();
    private Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentStorageLists = new LinkedHashMap<>();

    @Override
    public void directAdd(ICraftingMonitorElement element) {
        elements.add(element);
    }

    @Override
    public void addStorage(ICraftingMonitorElement element) {
        Map<Integer, ICraftingMonitorElement> craftingElements = currentCraftingLists.get(element.getId());
        Map<Integer, ICraftingMonitorElement> processingElements = currentProcessingLists.get(element.getId());
        Map<Integer, ICraftingMonitorElement> storedElements = currentStorageLists.get(element.getId());
        boolean merged = false;
        if (craftingElements != null) {
            ICraftingMonitorElement existingElement = craftingElements.get(element.elementHashCode());
            if (existingElement != null) {
                existingElement.merge(element);
                merged = true;
            }
        }
        if (processingElements != null) {
            ICraftingMonitorElement existingElement = processingElements.get(element.elementHashCode());
            if (existingElement != null) {
                existingElement.merge(element);
                merged = true;
            }
        }
        if (!merged) {
            if (storedElements == null) {
                storedElements = new HashMap<>();
            }
            storedElements.put(element.elementHashCode(), element);
            currentStorageLists.put(element.getId(), storedElements);
        }
    }

    @Override
    public void add(ICraftingMonitorElement element, boolean isProcessing) {
        Map<Integer, ICraftingMonitorElement> currentElements = isProcessing ? currentProcessingLists.get(element.getId()) : currentCraftingLists.get(element.getId());

        if (currentElements == null) {
            currentElements = new LinkedHashMap<>();
        }

        ICraftingMonitorElement existingElement = currentElements.get(element.elementHashCode());

        if (existingElement == null) {
            existingElement = element;
        } else {
            existingElement.merge(element);
        }

        currentElements.put(existingElement.elementHashCode(), existingElement);
        if (isProcessing) {
            currentProcessingLists.put(existingElement.getId(), currentElements);
        } else {
            currentCraftingLists.put(existingElement.getId(), currentElements);
        }

    }

    @Override
    public void commit() {
        currentCraftingLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentCraftingLists.clear();
        currentProcessingLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentProcessingLists.clear();
        currentStorageLists.values().stream().map(Map::values).flatMap(Collection::stream).forEach(elements::add);
        currentStorageLists.clear();
    }

    @Override
    public List<ICraftingMonitorElement> getElements() {
        if (!currentCraftingLists.isEmpty() || !currentProcessingLists.isEmpty() || !currentStorageLists.isEmpty()) {
            commit();
        }

        return elements;
    }
}

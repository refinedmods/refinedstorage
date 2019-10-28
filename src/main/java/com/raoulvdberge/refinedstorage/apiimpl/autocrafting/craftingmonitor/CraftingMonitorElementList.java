package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class CraftingMonitorElementList implements ICraftingMonitorElementList {
    private List<ICraftingMonitorElement> elements = new LinkedList<>();
    private Map<ResourceLocation, Map<Integer, ICraftingMonitorElement>> currentLists = new LinkedHashMap<>();

    @Override
    public void directAdd(ICraftingMonitorElement element) {
        elements.add(element);
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
    }

    @Override
    public List<ICraftingMonitorElement> getElements() {
        if (!currentLists.isEmpty()) {
            commit();
        }

        return elements;
    }
}

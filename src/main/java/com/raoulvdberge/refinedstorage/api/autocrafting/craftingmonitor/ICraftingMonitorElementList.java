package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;

import java.util.Collection;
import java.util.List;

public interface ICraftingMonitorElementList {
    /**
     * Directly add to the underlying list without trying to merge
     *
     * @param element the {@link ICraftingMonitorElement}
     */
    void directAdd(ICraftingMonitorElement element);

    /**
     * Add a element to the list
     * Similar elements will be merged
     * A {@link #commit()} will stop any following adds to be merged with previous ones
     *
     * @param element the {@link ICraftingMonitorElement}
     */
    void add(ICraftingMonitorElement element);

    /**
     * @param elements a {@link Collection} of {@link ICraftingMonitorElement}s to be added
     */
    default void addAll(Collection<ICraftingMonitorElement> elements) {
        elements.forEach(this::add);
    }

    /**
     * Finish a current merge operation
     */
    void commit();

    /**
     * This also commits the last changes
     *
     * @return Get the current list of elements
     */
    List<ICraftingMonitorElement> getElements();
}

package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;
import java.util.List;

public interface ICraftingMonitorElementList {
    /**
     * Directly add to the underlying list without trying to merge.
     *
     * @param element the {@link ICraftingMonitorElement}
     */
    void directAdd(ICraftingMonitorElement element);

    /**
     * Add a element to the list, similar elements will be merged.
     * A {@link #commit()} will stop any following adds to be merged with previous ones.
     *
     * @param isProcessing merge with Crafting or Processing List
     * @param element the {@link ICraftingMonitorElement}
     */
    void add(ICraftingMonitorElement element, boolean isProcessing);

    /**
     * Add Element to the Storage list
     * Elements already existing in Crafting or Processing list will be merged
     *
     * @param element the {@link ICraftingMonitorElement}
     */
    void addStorage(ICraftingMonitorElement element);

    /**
     * Finishes a current merge operation.
     */
    void commit();

    /**
     * Gets all the elements in the list.
     * This also commits the last changes.
     *
     * @return the current list of elements
     */
    List<ICraftingMonitorElement> getElements();


}

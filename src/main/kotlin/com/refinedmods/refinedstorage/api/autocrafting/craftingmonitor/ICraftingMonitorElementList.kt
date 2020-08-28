package com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor


interface ICraftingMonitorElementList {
    /**
     * Directly add to the underlying list without trying to merge.
     *
     * @param element the [ICraftingMonitorElement]
     */
    fun directAdd(element: ICraftingMonitorElement)

    /**
     * Add a element to the Storage list, similar elements will be merged.
     * A [.commit] will stop any following adds to be merged with previous ones.
     *
     * @param element the [ICraftingMonitorElement]
     */
    fun addStorage(element: ICraftingMonitorElement)

    /**
     * Add a element to the Processing or Crafting list, similar elements will be merged.
     * A [.commit] will stop any following adds to be merged with previous ones.
     *
     * @param element      the [ICraftingMonitorElement]
     * @param isProcessing whether to add to the processing list or the crafting list
     */
    fun add(element: ICraftingMonitorElement, isProcessing: Boolean)

    /**
     * Add a element to the list, similar elements will be merged.
     * A [.commit] will stop any following adds to be merged with previous ones.
     *
     * @param element the [ICraftingMonitorElement]
     */
    fun add(element: ICraftingMonitorElement)

    /**
     * Finishes a current merge operation.
     */
    fun commit()

    /**
     * Gets all the elements in the list.
     * This also commits the last changes.
     *
     * @return the current list of elements
     */
    fun getElements(): List<ICraftingMonitorElement>
}
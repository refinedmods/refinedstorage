package refinedstorage.api.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;

import java.util.List;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    String NBT_QUANTITY = "Quantity";
    String NBT_PATTERN_ID = "PatternID";
    String NBT_PATTERN_STACK = "PatternStack";
    String NBT_PATTERN_CONTAINER = "PatternContainer";

    /**
     * Calculates what this task will do, but doesn't run the task just yet.
     */
    void calculate();

    /**
     * Called when this task is cancelled.
     */
    void onCancelled();

    /**
     * Updates this task. Gets called every few ticks, depending on the speed of the pattern container.
     * {@link ICraftingTask#calculate()}  must be run before this
     *
     * @return true if this crafting task is finished and can be deleted from the list, false otherwise
     */
    boolean update();

    /**
     * @return the amount of items that have to be crafted
     */
    int getQuantity();

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * {@link ICraftingTask#calculate()} must be run before this
     *
     * @return the elements of this task for display in the crafting monitor
     */
    List<ICraftingMonitorElement> getCraftingMonitorElements();

    /**
     * @return the crafting pattern corresponding to this task
     */
    ICraftingPattern getPattern();

    /**
     * {@link ICraftingTask#calculate()} must be run before this
     *
     * @return the processable items in this task
     */
    List<IProcessable> getToProcess();

    /**
     * Used to check if the crafting task has recursive elements
     * (eg. block needs 9 ingots, ingots are crafted by a block)
     * {@link ICraftingTask#calculate()} must be run before this
     *
     * @return true if no recursion was found
     */
    boolean isValid();

    /**
     * {@link ICraftingTask#calculate()} must be run before this
     *
     * @return get a list of {@link ICraftingPreviewElement}s
     */
    List<ICraftingPreviewElement> getPreviewStacks();
}

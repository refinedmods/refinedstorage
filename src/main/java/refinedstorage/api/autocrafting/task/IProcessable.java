package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;

import java.util.Deque;

/**
 * Represents a item in a crafting task that can be processed.
 */
public interface IProcessable {
    /**
     * @return the pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return the stacks to insert
     */
    Deque<ItemStack> getToInsert();

    /**
     * @return true if we received all outputs, false otherwise
     */
    boolean hasReceivedOutputs();

    /**
     * @param i the output to check
     * @return true if we received the given output, false otherwise
     */
    boolean hasReceivedOutput(int i);

    /**
     * @param stack the stack that was inserted in the storage system
     * @return true if this item belonged to the processable item, false otherwise
     */
    boolean onReceiveOutput(ItemStack stack);
}

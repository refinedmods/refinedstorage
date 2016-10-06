package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;

/**
 * Represents a item in a crafting task that can be processed.
 */
public interface IProcessable {
    /**
     * @return the pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return the first stack to attempt inserting
     */
    ItemStack getStackToInsert();

    /**
     * Goes to the next stack to insert.
     */
    void nextStack();

    /**
     * @return whether this processable item has received its items
     */
    boolean hasReceivedOutputs();

    /**
     * @param stack the stack that was inserted in the storage system
     * @return whether this item belonged to the processable item
     */
    boolean onReceiveOutput(ItemStack stack);
}

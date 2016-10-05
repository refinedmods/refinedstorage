package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;

public interface IProcessable {
    ICraftingPattern getPattern();

    ItemStack getStackToInsert();

    void nextStack();

    boolean hasReceivedOutputs();

    boolean onReceiveOutput(ItemStack stack);
}

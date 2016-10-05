package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;

import java.util.Deque;

public interface IProcessable {
    ICraftingPattern getPattern();

    Deque<ItemStack> getToInsert();
}

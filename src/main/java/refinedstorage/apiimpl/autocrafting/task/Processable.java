package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.IProcessable;

import java.util.ArrayDeque;
import java.util.Deque;

public class Processable implements IProcessable {
    private ICraftingPattern pattern;
    private Deque<ItemStack> toInsert = new ArrayDeque<>();

    public Processable(ICraftingPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public Deque<ItemStack> getToInsert() {
        return toInsert;
    }

    @Override
    public String toString() {
        return "ProcessablePattern{" +
                "pattern=" + pattern +
                ", toInsert=" + toInsert +
                '}';
    }
}

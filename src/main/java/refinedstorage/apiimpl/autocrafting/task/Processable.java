package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.apiimpl.API;

import java.util.ArrayDeque;
import java.util.Deque;

public class Processable implements IProcessable {
    private ICraftingPattern pattern;
    private Deque<ItemStack> toInsert = new ArrayDeque<>();
    private boolean satisfied[];

    public Processable(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];

        for (ItemStack input : pattern.getInputs()) {
            if (input != null) {
                toInsert.add(input.copy());
            }
        }
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
    public boolean hasReceivedOutputs() {
        for (boolean item : satisfied) {
            if (!item) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasReceivedOutput(int i) {
        return satisfied[i];
    }

    @Override
    public boolean onReceiveOutput(ItemStack stack) {
        for (int i = 0; i < pattern.getOutputs().size(); ++i) {
            if (!satisfied[i]) {
                ItemStack item = pattern.getOutputs().get(i);

                if (API.instance().getComparer().isEqualNoQuantity(stack, item)) {
                    satisfied[i] = true;

                    return true;
                }
            }
        }

        return false;
    }
}

package refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.apiimpl.API;

public class Processable implements IProcessable {
    private ICraftingPattern pattern;
    private int pos;
    private boolean satisfied[];

    public Processable(ICraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().size()];
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public void nextStack() {
        ++pos;
    }

    @Override
    public ItemStack getStackToInsert() {
        if (pos > pattern.getInputs().size() - 1) {
            return null;
        }

        return pattern.getInputs().get(pos);
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

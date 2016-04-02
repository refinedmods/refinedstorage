package refinedstorage.jei;

import mezz.jei.plugins.vanilla.VanillaRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class SoldererRecipeWrapper extends VanillaRecipeWrapper {
    private List<ItemStack> inputs;
    private ItemStack output;

    public SoldererRecipeWrapper(List<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public List<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return Collections.singletonList(output);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SoldererRecipeWrapper)) {
            return false;
        }

        SoldererRecipeWrapper other = (SoldererRecipeWrapper) obj;

        for (int i = 0; i < inputs.size(); i++) {
            if (!ItemStack.areItemStacksEqual(inputs.get(i), other.inputs.get(i))) {
                return false;
            }
        }

        return ItemStack.areItemStacksEqual(output, other.output);
    }

    @Override
    public String toString() {
        return inputs + " = " + output;
    }
}

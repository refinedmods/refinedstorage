package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import refinedstorage.util.InventoryUtils;

public class CraftingPattern {
    private TileCrafter crafter;
    private boolean processing;
    private ItemStack[] inputs;
    private ItemStack[] outputs;

    public CraftingPattern(TileCrafter crafter, boolean processing, ItemStack[] inputs, ItemStack[] outputs) {
        this.crafter = crafter;
        this.processing = processing;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public TileCrafter getCrafter() {
        return crafter;
    }

    public boolean isProcessing() {
        return processing;
    }

    public ItemStack[] getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public boolean comparePattern(CraftingPattern otherPattern, int flags) {
        if (otherPattern == this) {
            return true;
        }

        if (otherPattern.getInputs().length != inputs.length ||
            otherPattern.getOutputs().length != outputs.length ||
            otherPattern.isProcessing() != processing ||
            !otherPattern.getCrafter().getPos().equals(crafter.getPos())) {
            return false;
        }

        for (int i = 0; i < inputs.length; ++i) {
            if (!InventoryUtils.compareStack(inputs[i], otherPattern.getInputs()[i], flags)) {
                return false;
            }
        }

        for (int i = 0; i < outputs.length; ++i) {
            if (!InventoryUtils.compareStack(outputs[i], otherPattern.getOutputs()[i], flags)) {
                return false;
            }
        }

        return true;
    }
}

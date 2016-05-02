package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;

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
}

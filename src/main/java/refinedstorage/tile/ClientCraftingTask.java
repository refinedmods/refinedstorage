package refinedstorage.tile;

import net.minecraft.item.ItemStack;

public class ClientCraftingTask {
    private ItemStack output;
    private int id;
    private String info;

    // Used server-side while sending
    private ItemStack[] outputs;

    public ClientCraftingTask(ItemStack output, int id, String info) {
        this.output = output;
        this.id = id;
        this.info = info;
    }

    public ClientCraftingTask(String info, ItemStack[] outputs) {
        this.info = info;
        this.outputs = outputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }
}

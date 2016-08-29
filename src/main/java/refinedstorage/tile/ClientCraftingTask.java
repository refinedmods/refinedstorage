package refinedstorage.tile;

import net.minecraft.item.ItemStack;

public class ClientCraftingTask {
    private ItemStack output;
    private int id;
    private String status;

    // Used server-side while sending
    private ItemStack[] outputs;

    public ClientCraftingTask(ItemStack output, int id, String status) {
        this.output = output;
        this.id = id;
        this.status = status;
    }

    public ClientCraftingTask(String status, ItemStack[] outputs) {
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}

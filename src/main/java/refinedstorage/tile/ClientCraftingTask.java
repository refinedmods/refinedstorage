package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.task.ICraftingTask;

public class ClientCraftingTask {
    private ItemStack output;
    private int id;
    private String status;
    private int depth;

    // Used server-side while sending
    private ItemStack[] outputs;
    private ClientCraftingTask child;

    public ClientCraftingTask(ItemStack output, int id, String status, int depth) {
        this.output = output;
        this.id = id;
        this.status = status;
        this.depth = depth;
    }

    public ClientCraftingTask(String status, ItemStack[] outputs, ICraftingTask child) {
        this.status = status;
        this.outputs = outputs;
        this.child = child != null ? new ClientCraftingTask(child.getStatus(), child.getPattern().getOutputs(), child.getChild()) : null;
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

    public ClientCraftingTask getChild() {
        return child;
    }

    public int getDepth() {
        return depth;
    }
}

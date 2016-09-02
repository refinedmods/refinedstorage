package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.task.ICraftingTask;

import java.util.List;

public class ClientCraftingTask {
    private ItemStack output;
    private int id;
    private String status;
    private int depth;
    private int progress;

    // Used server-side while sending
    private List<ItemStack> outputs;
    private ClientCraftingTask child;

    public ClientCraftingTask(ItemStack output, int id, String status, int depth, int progress) {
        this.output = output;
        this.id = id;
        this.status = status;
        this.depth = depth;
        this.progress = progress;
    }

    public ClientCraftingTask(String status, List<ItemStack> outputs, int progress, ICraftingTask child) {
        this.status = status;
        this.outputs = outputs;
        this.progress = progress;
        this.child = child != null ? new ClientCraftingTask(child.getStatus(), child.getPattern().getOutputs(), child.getProgress(), child.getChild()) : null;
    }

    public ItemStack getOutput() {
        return output;
    }

    public List<ItemStack> getOutputs() {
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

    public int getProgress() {
        return progress;
    }
}

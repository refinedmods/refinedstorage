package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;

import java.util.List;

public class ClientCraftingTask {
    private ItemStack output;
    private int id;
    private String status;
    private int depth;
    private int children;
    private int progress;

    // Used server-side while sending
    private List<ItemStack> outputs;
    private ClientCraftingTask child;

    public ClientCraftingTask(ItemStack output, int id, String status, int depth, int children, int progress) {
        this.output = output;
        this.id = id;
        this.status = status;
        this.depth = depth;
        this.children = children;
        this.progress = progress;
    }

    public ClientCraftingTask(INetworkMaster network, ICraftingTask task) {
        this.status = task.getStatus(network);
        this.outputs = task.getPattern().getOutputs();
        this.progress = task.getProgress();
        this.child = task.getChild() != null ? new ClientCraftingTask(network, task.getChild()) : null;
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

    public int getChildren() {
        return children;
    }

    public int getProgress() {
        return progress;
    }
}

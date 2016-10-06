package refinedstorage.tile;

import net.minecraft.item.ItemStack;

public class ClientCraftingTask {
    private ItemStack output;
    private int quantity;

    public ClientCraftingTask(ItemStack output, int quantity) {
        this.output = output;
        this.quantity = quantity;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getQuantity() {
        return quantity;
    }
}

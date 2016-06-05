package refinedstorage.tile;

import net.minecraft.item.ItemStack;

public final class ClientItem {
    private int id;
    private ItemStack stack;

    public ClientItem(int id, ItemStack stack) {
        this.id = id;
        this.stack = stack;
    }

    public int getId() {
        return id;
    }

    public ItemStack getStack() {
        return stack;
    }
}

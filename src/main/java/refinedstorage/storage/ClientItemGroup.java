package refinedstorage.storage;

import net.minecraft.item.ItemStack;

public final class ClientItemGroup {
    private int id;
    private ItemStack stack;

    public ClientItemGroup(int id, ItemStack stack) {
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
package refinedstorage.apiimpl.storage;

import net.minecraft.item.ItemStack;

public class ClientStack {
    private int id;
    private ItemStack stack;
    private boolean craftable;

    public ClientStack(int id, ItemStack stack, boolean craftable) {
        this.id = id;
        this.stack = stack;
        this.craftable = craftable;
    }

    public int getId() {
        return id;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientStack && ((ClientStack) obj).getId() == id;
    }
}

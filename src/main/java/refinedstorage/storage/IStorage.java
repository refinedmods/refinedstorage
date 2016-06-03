package refinedstorage.storage;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IStorage {
    void addItems(List<ItemStack> items);

    void push(ItemStack stack);

    ItemStack take(ItemStack stack, int flags);

    boolean mayPush(ItemStack stack);

    int getPriority();
}

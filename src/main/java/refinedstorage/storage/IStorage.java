package refinedstorage.storage;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IStorage {
    void addItems(List<ItemGroup> items);

    void push(ItemStack stack);

    ItemStack take(ItemStack stack, int flags);

    boolean canPush(ItemStack stack);

    int getPriority();
}

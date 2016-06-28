package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IItemList {
    void add(ItemStack stack);

    void remove(ItemStack stack);

    ItemStack get(ItemStack stack, int flags);

    List<ItemStack> getStacks();
}

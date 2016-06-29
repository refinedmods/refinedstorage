package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.api.network.INetworkMaster;

import java.util.List;

public interface IItemList {
    void rebuild(INetworkMaster master);

    void add(ItemStack stack);

    void remove(ItemStack stack);

    ItemStack get(ItemStack stack, int flags);

    List<ItemStack> getStacks();
}

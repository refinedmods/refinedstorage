package refinedstorage.api.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface IItemStackList {
    void add(ItemStack stack);

    boolean remove(@Nonnull ItemStack stack, boolean removeIfReachedZero);

    @Nullable
    ItemStack get(@Nonnull ItemStack stack, int flags);

    @Nullable
    ItemStack get(int hash);

    void clear();

    boolean isEmpty();

    @Nonnull
    Collection<ItemStack> getStacks();

    @Nonnull
    IItemStackList copy();
}

package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemHandlerBase extends ItemStackHandler {
    @Nullable
    private Consumer<Integer> listener;

    private boolean empty = true;

    protected Predicate<ItemStack>[] validators;

    public ItemHandlerBase(int size, @Nullable Consumer<Integer> listener, Predicate<ItemStack>... validators) {
        super(size);

        this.listener = listener;
        this.validators = validators;
    }

    public ItemHandlerBase(int size, Predicate<ItemStack>... validators) {
        this(size, null, validators);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);

        stacks.set(slot, stack);

        onContentsChanged(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (validators.length > 0) {
            for (Predicate<ItemStack> validator : validators) {
                if (validator.test(stack)) {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            return stack;
        }

        return super.insertItem(slot, stack, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (listener != null) {
            listener.accept(slot);
        }

        this.empty = stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isEmpty() {
        return empty;
    }
}

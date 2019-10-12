package com.raoulvdberge.refinedstorage.inventory.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BaseItemHandler extends ItemStackHandler {
    @Nullable
    private Consumer<Integer> listener;

    private boolean empty = true;

    protected Predicate<ItemStack>[] validators;

    private boolean reading;

    public BaseItemHandler(int size, @Nullable Consumer<Integer> listener, Predicate<ItemStack>... validators) {
        super(size);

        this.listener = listener;
        this.validators = validators;
    }

    public BaseItemHandler(int size, Predicate<ItemStack>... validators) {
        this(size, null, validators);
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

        if (!reading && listener != null) {
            listener.accept(slot);
        }

        this.empty = stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        super.deserializeNBT(tag);

        this.empty = stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }
}

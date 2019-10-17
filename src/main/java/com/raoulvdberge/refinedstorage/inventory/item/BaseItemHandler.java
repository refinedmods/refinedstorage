package com.raoulvdberge.refinedstorage.inventory.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

// TODO Builder system for server and clientside listeners.
public class BaseItemHandler extends ItemStackHandler {
    private final Consumer<Integer> listener;
    private final List<Predicate<ItemStack>> validators = new ArrayList<>();

    private boolean empty = true;
    private boolean reading;

    public BaseItemHandler(int size, @Nullable Consumer<Integer> listener) {
        super(size);

        this.listener = listener;
    }

    public BaseItemHandler addValidator(Predicate<ItemStack> validator) {
        validators.add(validator);

        return this;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!validators.isEmpty()) {
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

    public boolean isReading() {
        return reading;
    }
}

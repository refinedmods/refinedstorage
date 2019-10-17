package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.inventory.listener.InventoryListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BaseItemHandler extends ItemStackHandler {
    private final List<InventoryListener<BaseItemHandler>> listeners = new ArrayList<>();
    private final List<Predicate<ItemStack>> validators = new ArrayList<>();

    private boolean empty = true;
    private boolean reading;

    public BaseItemHandler(int size) {
        super(size);
    }

    public BaseItemHandler addValidator(Predicate<ItemStack> validator) {
        validators.add(validator);

        return this;
    }

    public BaseItemHandler addListener(InventoryListener<BaseItemHandler> listener) {
        listeners.add(listener);

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

        this.empty = stacks.stream().allMatch(ItemStack::isEmpty);
        this.listeners.forEach(l -> l.onChanged(this, slot, reading));
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

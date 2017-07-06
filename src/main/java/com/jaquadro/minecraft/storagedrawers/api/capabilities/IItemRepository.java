package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public interface IItemRepository {
    /**
     * Gets a list of all items in the inventory.  The same item may appear multiple times with varying counts.
     *
     * @return A list of zero or more items in the inventory.
     */
    @Nonnull
    NonNullList<ItemRecord> getAllItems();

    /**
     * Inserts an ItemStack into the inventory and returns the remainder.
     *
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted.  If the entire stack was accepted, returns
     * ItemStack.EMPTY instead.
     */
    @Nonnull
    ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate);

    /**
     * Tries to extract the given ItemStack from the inventory.  The returned value will be a matching ItemStack
     * with a stack size equal to or less than amount, or the empty ItemStack if the item could not be found at all.
     * The returned stack size may exceed the itemstack's getMaxStackSize() value.
     *
     * @param stack    The item to extract.  The stack size is ignored.
     * @param amount   Amount to extract (may be greater than the stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the inventory, or ItemStack.EMPTY if nothing could be extracted.
     */
    @Nonnull
    ItemStack extractItem(@Nonnull ItemStack stack, int amount, boolean simulate);

    /**
     * An item record representing an item and the amount stored.
     * <p>
     * The ItemStack held by itemPrototype always reports a stack size of 1.
     * IT IS IMPORTANT THAT YOU NEVER MODIFY itemPrototype.
     */
    class ItemRecord {
        @Nonnull
        public final ItemStack itemPrototype;
        public final int count;

        public ItemRecord(@Nonnull ItemStack itemPrototype, int count) {
            this.itemPrototype = itemPrototype;
            this.count = count;
        }
    }
}

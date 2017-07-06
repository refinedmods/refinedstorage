package com.jaquadro.minecraft.storagedrawers.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class Drawers {
    public static final IDrawer DISABLED = new DisabledDrawer();
    public static final IFractionalDrawer DISABLED_FRACTIONAL = new DisabledFractionalDrawer();

    private static class DisabledDrawer implements IDrawer {
        @Nonnull
        @Override
        public ItemStack getStoredItemPrototype() {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public IDrawer setStoredItem(@Nonnull ItemStack itemPrototype) {
            return this;
        }

        @Override
        public int getStoredItemCount() {
            return 0;
        }

        @Override
        public void setStoredItemCount(int amount) {

        }

        @Override
        public int getMaxCapacity(@Nonnull ItemStack itemPrototype) {
            return 0;
        }

        @Override
        public int getRemainingCapacity() {
            return 0;
        }

        @Override
        public boolean canItemBeStored(@Nonnull ItemStack itemPrototype) {
            return false;
        }

        @Override
        public boolean canItemBeExtracted(@Nonnull ItemStack itemPrototype) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    private static class DisabledFractionalDrawer extends DisabledDrawer implements IFractionalDrawer {
        @Override
        public int getConversionRate() {
            return 0;
        }

        @Override
        public int getStoredItemRemainder() {
            return 0;
        }

        @Override
        public boolean isSmallestUnit() {
            return false;
        }
    }
}

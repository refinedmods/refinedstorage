package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import com.raoulvdberge.refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import net.minecraft.item.ItemStack;

public interface IItemValidator {
    IItemValidator ITEM_STORAGE_DISK = new ItemValidatorBasic(RSItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    };
    IItemValidator FLUID_STORAGE_DISK = new ItemValidatorBasic(RSItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    };
    IItemValidator STORAGE_DISK = new IItemValidator() {
        @Override
        public boolean isValid(ItemStack stack) {
            return ITEM_STORAGE_DISK.isValid(stack) || FLUID_STORAGE_DISK.isValid(stack);
        }
    };

    boolean isValid(ItemStack stack);
}

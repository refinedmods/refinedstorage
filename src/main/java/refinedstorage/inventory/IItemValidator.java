package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;

public interface IItemValidator {
    IItemValidator itemStorageDisk = new ItemValidatorBasic(RefinedStorageItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    };
    IItemValidator fluidStorageDisk = new ItemValidatorBasic(RefinedStorageItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    };
    IItemValidator storageDisk = new IItemValidator() {
        @Override
        public boolean isValid(ItemStack stack) {
            return itemStorageDisk.isValid(stack) || fluidStorageDisk.isValid(stack);
        }
    };

    boolean isValid(ItemStack stack);
}

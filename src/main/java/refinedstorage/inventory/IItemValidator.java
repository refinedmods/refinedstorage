package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;

public interface IItemValidator {
    IItemValidator ITEM_STORAGE_DISK = new ItemValidatorBasic(RefinedStorageItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    };
    IItemValidator FLUID_STORAGE_DISK = new ItemValidatorBasic(RefinedStorageItems.FLUID_STORAGE_DISK) {
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

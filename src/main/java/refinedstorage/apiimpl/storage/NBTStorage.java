package refinedstorage.apiimpl.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;

import java.util.function.Function;

public class NBTStorage {
    public static void constructFromDrive(ItemStack disk, int slot, ItemStorageNBT[] itemStorages, FluidStorageNBT[] fluidStorages, Function<ItemStack, ItemStorageNBT> itemStorageSupplier, Function<ItemStack, FluidStorageNBT> fluidStorageNBTSupplier) {
        if (disk == null) {
            itemStorages[slot] = null;
            fluidStorages[slot] = null;
        } else {
            if (disk.getItem() == RefinedStorageItems.STORAGE_DISK) {
                itemStorages[slot] = itemStorageSupplier.apply(disk);
            } else if (disk.getItem() == RefinedStorageItems.FLUID_STORAGE_DISK) {
                fluidStorages[slot] = fluidStorageNBTSupplier.apply(disk);
            }
        }
    }
}

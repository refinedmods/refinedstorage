package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemStorageDisk;
import refinedstorage.tile.TileDiskDrive;
import refinedstorage.tile.settings.ModeSettingUtils;

public class DiskStorage extends NBTStorage {
    private TileDiskDrive diskDrive;

    public DiskStorage(ItemStack disk, TileDiskDrive diskDrive) {
        super(disk.getTagCompound(), getCapacity(disk), diskDrive.getPriority());

        this.diskDrive = diskDrive;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        if (ModeSettingUtils.doesNotViolateMode(diskDrive.getInventory(), diskDrive.getModeSetting(), diskDrive.getCompare(), stack)) {
            return super.canPush(stack);
        }

        return false;
    }

    public static int getCapacity(ItemStack disk) {
        switch (disk.getItemDamage()) {
            case ItemStorageDisk.TYPE_1K:
                return 1000;
            case ItemStorageDisk.TYPE_4K:
                return 4000;
            case ItemStorageDisk.TYPE_16K:
                return 16000;
            case ItemStorageDisk.TYPE_64K:
                return 64000;
            case ItemStorageDisk.TYPE_CREATIVE:
                return -1;
        }

        return 0;
    }
}

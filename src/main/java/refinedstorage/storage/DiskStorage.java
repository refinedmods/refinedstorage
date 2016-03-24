package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemStorageDisk;
import refinedstorage.tile.TileDrive;
import refinedstorage.tile.settings.ModeSettingUtils;

public class DiskStorage extends NBTStorage {
    private TileDrive drive;

    public DiskStorage(ItemStack disk, TileDrive drive) {
        super(disk.getTagCompound(), getCapacity(disk), drive.getPriority());

        this.drive = drive;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        if (ModeSettingUtils.doesNotViolateMode(drive.getInventory(), drive.getModeSetting(), drive.getCompare(), stack)) {
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

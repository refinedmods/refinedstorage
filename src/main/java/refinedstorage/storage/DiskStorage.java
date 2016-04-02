package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.block.EnumStorageType;
import refinedstorage.tile.TileDiskDrive;
import refinedstorage.tile.config.ModeConfigUtils;

public class DiskStorage extends NBTStorage {
    private TileDiskDrive diskDrive;

    public DiskStorage(ItemStack disk, TileDiskDrive diskDrive) {
        super(disk.getTagCompound(), EnumStorageType.getById(disk.getItemDamage()).getCapacity(), diskDrive.getPriority());

        this.diskDrive = diskDrive;
    }

    @Override
    public boolean canPush(ItemStack stack) {
        if (ModeConfigUtils.doesNotViolateMode(diskDrive.getInventory(), diskDrive.getModeConfig(), diskDrive.getCompare(), stack)) {
            return super.canPush(stack);
        }

        return false;
    }
}

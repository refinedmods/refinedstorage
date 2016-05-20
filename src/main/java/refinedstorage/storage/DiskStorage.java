package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.block.EnumStorageType;
import refinedstorage.tile.TileDiskDrive;
import refinedstorage.tile.config.ModeConfigUtils;

public class DiskStorage extends NBTStorage {
    private TileDiskDrive diskDrive;

    public DiskStorage(ItemStack disk, TileDiskDrive diskDrive) {
        super(disk.getTagCompound(), EnumStorageType.getById(disk.getItemDamage()).getCapacity());

        this.diskDrive = diskDrive;
    }

    @Override
    public int getPriority() {
        return diskDrive.getPriority();
    }

    @Override
    public boolean mayPush(ItemStack stack) {
        return ModeConfigUtils.doesNotViolateMode(diskDrive.getInventory(), diskDrive.getModeConfig(), diskDrive.getCompare(), stack) && super.mayPush(stack);
    }
}

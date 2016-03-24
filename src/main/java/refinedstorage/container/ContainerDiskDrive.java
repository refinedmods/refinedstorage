package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.BasicItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.tile.TileDiskDrive;

public class ContainerDiskDrive extends ContainerStorage {
    public ContainerDiskDrive(EntityPlayer player, TileDiskDrive drive) {
        super(player, drive.getInventory());

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(drive, i, 98 + (i * 18), 78, new BasicItemValidator(RefinedStorageItems.STORAGE_DISK)));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(drive, 4 + i, 98 + (i * 18), 96, new BasicItemValidator(RefinedStorageItems.STORAGE_DISK)));
        }
    }
}

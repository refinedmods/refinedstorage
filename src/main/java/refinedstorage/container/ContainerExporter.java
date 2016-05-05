package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.IItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileExporter;

public class ContainerExporter extends ContainerBase {
    public ContainerExporter(EntityPlayer player, TileExporter exporter) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(exporter.getInventory(), i, 8 + (18 * i), 20, false));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(exporter.getUpgradesInventory(), i, 187, 6 + (i * 18), new IItemValidator() {
                @Override
                public boolean isValid(ItemStack stack) {
                    return stack.getItem() == RefinedStorageItems.UPGRADE && (stack.getMetadata() == ItemUpgrade.TYPE_SPEED || stack.getMetadata() == ItemUpgrade.TYPE_CRAFTING);
                }
            }));
        }

        addPlayerInventory(8, 55);
    }
}

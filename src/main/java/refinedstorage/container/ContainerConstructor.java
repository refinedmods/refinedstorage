package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.IItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {

    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        addSlotToContainer(new SlotSpecimenItemBlock(constructor.getInventory(), 0, 80, 20));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(constructor.getUpgradesInventory(), i, 187, 6 + (i * 18), new IItemValidator() {
                @Override
                public boolean isValid(ItemStack stack) {
                    return stack.getItem() == RefinedStorageItems.UPGRADE && (stack.getMetadata() == ItemUpgrade.TYPE_SPEED || stack.getMetadata() == ItemUpgrade.TYPE_CRAFTING);
                }
            }));
        }

        addPlayerInventory(8, 55);
    }
}

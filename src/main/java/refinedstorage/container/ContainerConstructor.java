package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {
    class SlotConstructor extends SlotSpecimen {
        public SlotConstructor(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y, false);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof ItemBlock;
        }
    }

    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        addSlotToContainer(new SlotConstructor(constructor.getInventory(), 0, 80, 20));

        addPlayerInventory(8, 55);
    }
}

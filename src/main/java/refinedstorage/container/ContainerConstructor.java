package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {

    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        addSlotToContainer(new SlotSpecimenItemBlock(constructor.getInventory(), 0, 80, 20));

        addPlayerInventory(8, 55);
    }
}

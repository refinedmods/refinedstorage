package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileDetector;

public class ContainerDetector extends ContainerBase {
    public ContainerDetector(EntityPlayer player, TileDetector detector) {
        super(player);

        addSlotToContainer(new SlotSpecimen(detector.getInventory(), 0, 107, 20));

        addPlayerInventory(8, 55);
    }
}

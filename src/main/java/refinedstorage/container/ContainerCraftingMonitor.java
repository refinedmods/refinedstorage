package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.tile.TileCraftingMonitor;

public class ContainerCraftingMonitor extends ContainerBase {
    public ContainerCraftingMonitor(TileCraftingMonitor craftingMonitor, EntityPlayer player) {
        super(craftingMonitor, player);

        addPlayerInventory(8, 148);
    }
}

package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerCraftingMonitor extends ContainerBase {
    public ContainerCraftingMonitor(EntityPlayer player) {
        super(player);

        addPlayerInventory(8, 129);
    }
}

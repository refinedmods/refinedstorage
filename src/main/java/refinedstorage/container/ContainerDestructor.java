package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerDestructor extends ContainerBase {
    public ContainerDestructor(EntityPlayer player) {
        super(player);

        addPlayerInventory(8, 50);
    }
}

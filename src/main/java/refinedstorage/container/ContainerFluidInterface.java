package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimenFluid;
import refinedstorage.tile.TileFluidInterface;

public class ContainerFluidInterface extends ContainerBase {
    public ContainerFluidInterface(TileFluidInterface fluidInterface, EntityPlayer player) {
        super(fluidInterface, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(fluidInterface.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotItemHandler(fluidInterface.getIn(), 0, 44, 32));
        addSlotToContainer(new SlotSpecimenFluid(!fluidInterface.getWorld().isRemote, fluidInterface.getOut(), 0, 116, 32));

        addPlayerInventory(8, 122);
    }
}

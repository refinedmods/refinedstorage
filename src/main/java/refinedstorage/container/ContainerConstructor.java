package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimenItemBlock;
import refinedstorage.tile.TileConstructor;

public class ContainerConstructor extends ContainerBase {

    public ContainerConstructor(EntityPlayer player, TileConstructor constructor) {
        super(player);

        addSlotToContainer(new SlotSpecimenItemBlock(constructor.getFilter(), 0, 80, 20));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(constructor.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);
    }
}

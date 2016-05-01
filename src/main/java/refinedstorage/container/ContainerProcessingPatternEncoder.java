package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.BasicItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.autocrafting.TileProcessingPatternEncoder;

public class ContainerProcessingPatternEncoder extends ContainerBase {
    public ContainerProcessingPatternEncoder(EntityPlayer player, TileProcessingPatternEncoder ppEncoder) {
        super(player);

        int ox = 8;
        int x = ox;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlotToContainer(new SlotSpecimen(ppEncoder.getInputsOutputsInventory(), i, x, y, false));

            x += 18;

            if ((i + 1) % 3 == 0) {
                if (i == 8) {
                    ox = 90;
                    x = ox;
                    y = 20;
                } else {
                    x = ox;
                    y += 18;
                }
            }
        }

        addSlotToContainer(new SlotFiltered(ppEncoder, 0, 152, 18, new BasicItemValidator(RefinedStorageItems.PATTERN)));
        addSlotToContainer(new SlotOutput(ppEncoder, 1, 152, 58));

        addPlayerInventory(8, 90);
    }
}

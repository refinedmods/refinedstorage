package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCrafter extends ContainerBase {
    public ContainerCrafter(TileCrafter crafter, PlayerEntity player) {
        super(crafter, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotItemHandler(crafter.getNode().getPatternItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(crafter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, crafter.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, crafter.getNode().getPatternItems());
    }
}

package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.tile.CrafterTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class CrafterContainer extends BaseContainer {
    public CrafterContainer(CrafterTile crafter, PlayerEntity player, int windowId) {
        super(RSContainers.CRAFTER, crafter, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotItemHandler(crafter.getNode().getPatternInventory(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(crafter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, crafter.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, crafter.getNode().getPatternInventory());
    }
}

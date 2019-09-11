package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDestructor extends ContainerBase {
    public ContainerDestructor(TileDestructor destructor, PlayerEntity player, int windowId) {
        super(RSContainers.DESTRUCTOR, destructor, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(destructor.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(destructor.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> destructor.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilterFluid(destructor.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> destructor.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, destructor.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, destructor.getNode().getItemFilters(), destructor.getNode().getFluidFilters(), destructor.getNode()::getType);
    }
}

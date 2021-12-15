package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.DestructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class DestructorContainerMenu extends BaseContainerMenu {
    public DestructorContainerMenu(DestructorBlockEntity destructor, Player player, int windowId) {
        super(RSContainerMenus.DESTRUCTOR, destructor, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(destructor.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(destructor.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> destructor.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(destructor.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> destructor.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.getInventory(), destructor.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.getInventory(), destructor.getNode().getItemFilters(), destructor.getNode().getFluidFilters(), destructor.getNode()::getType);
    }
}

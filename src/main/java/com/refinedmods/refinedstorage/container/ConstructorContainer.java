package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.ConstructorTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ConstructorContainer extends BaseContainer {
    public ConstructorContainer(ConstructorTile constructor, PlayerEntity player, int windowId) {
        super(RSContainers.CONSTRUCTOR, constructor, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(constructor.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlot(new FilterSlot(constructor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> constructor.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(constructor.getNode().getFluidFilters(), 0, 80, 20, 0).setEnableHandler(() -> constructor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, constructor.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, constructor.getNode().getItemFilters(), constructor.getNode().getFluidFilters(), constructor.getNode()::getType);
    }
}

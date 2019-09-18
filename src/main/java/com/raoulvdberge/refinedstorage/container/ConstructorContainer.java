package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ConstructorContainer extends BaseContainer {
    public ConstructorContainer(TileConstructor constructor, PlayerEntity player, int windowId) {
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

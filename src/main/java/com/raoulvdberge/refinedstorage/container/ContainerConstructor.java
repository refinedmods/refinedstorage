package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerConstructor extends ContainerBase {
    public ContainerConstructor(TileConstructor constructor, PlayerEntity player) {
        super(constructor, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(constructor.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotFilter(constructor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> constructor.getNode().getType() == IType.ITEMS));
        addSlotToContainer(new SlotFilterFluid(constructor.getNode().getFluidFilters(), 0, 80, 20, 0).setEnableHandler(() -> constructor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, constructor.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, constructor.getNode().getItemFilters(), constructor.getNode().getFluidFilters(), constructor.getNode()::getType);
    }
}

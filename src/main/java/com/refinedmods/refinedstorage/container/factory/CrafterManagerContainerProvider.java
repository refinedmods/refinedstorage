package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrafterManagerContainerProvider implements MenuProvider {
    private final CrafterManagerTile tile;

    public CrafterManagerContainerProvider(CrafterManagerTile tile) {
        this.tile = tile;
    }

    public static void writeToBuffer(FriendlyByteBuf buf, Level level, BlockPos pos) {
        buf.writeBlockPos(pos);

        Map<Component, List<IItemHandlerModifiable>> containerData = ((CrafterManagerTile) level.getBlockEntity(pos)).getNode().getNetwork().getCraftingManager().getNamedContainers();

        buf.writeInt(containerData.size());

        for (Map.Entry<Component, List<IItemHandlerModifiable>> entry : containerData.entrySet()) {
            buf.writeComponent(entry.getKey());

            int slots = 0;
            for (IItemHandlerModifiable handler : entry.getValue()) {
                slots += handler.getSlots();
            }

            buf.writeInt(slots);
        }
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("gui.refinedstorage.crafter_manager");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        CrafterManagerContainer container = new CrafterManagerContainer(tile, playerEntity, windowId);

        container.setScreenInfoProvider(new EmptyScreenInfoProvider());
        container.initSlotsServer();

        return container;
    }
}

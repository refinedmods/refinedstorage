package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainerMenu;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrafterManagerMenuProvider implements MenuProvider {
    private final CrafterManagerBlockEntity blockEntity;

    public CrafterManagerMenuProvider(CrafterManagerBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public static void writeToBuffer(FriendlyByteBuf buf, Level level, BlockPos pos) {
        buf.writeBlockPos(pos);

        Map<Component, List<IItemHandlerModifiable>> containerData = ((CrafterManagerBlockEntity) level.getBlockEntity(pos)).getNode().getNetwork().getCraftingManager().getNamedContainers();

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
        return Component.translatable("gui.refinedstorage.crafter_manager");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        CrafterManagerContainerMenu container = new CrafterManagerContainerMenu(blockEntity, playerEntity, windowId);

        container.setScreenInfoProvider(new EmptyScreenInfoProvider());
        container.initSlotsServer();

        return container;
    }
}

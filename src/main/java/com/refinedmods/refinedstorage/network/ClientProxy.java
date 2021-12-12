package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.CraftingMonitorScreen;
import com.refinedmods.refinedstorage.screen.grid.CraftingPreviewScreen;
import com.refinedmods.refinedstorage.screen.grid.CraftingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class ClientProxy {
    private ClientProxy() {
    }

    public static void onReceivedCraftingPreviewResponseMessage(GridCraftingPreviewResponseMessage message) {
        Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof CraftingSettingsScreen) {
            screen = ((CraftingSettingsScreen) screen).getParent();
        }

        Minecraft.getInstance().setScreen(new CraftingPreviewScreen(
            screen,
            message.getElements(),
            message.getId(),
            message.getQuantity(),
            message.isFluids(),
            new TranslatableComponent("gui.refinedstorage.crafting_preview")
        ));
    }

    public static void onReceivedCraftingStartResponseMessage() {
        Screen screen = Minecraft.getInstance().screen;

        if (screen instanceof CraftingSettingsScreen) {
            ((CraftingSettingsScreen) screen).close();
        }
    }

    public static void onReceivedCraftingMonitorUpdateMessage(CraftingMonitorUpdateMessage message) {
        BaseScreen.executeLater(CraftingMonitorScreen.class, craftingMonitor -> craftingMonitor.setTasks(message.getTasks()));
    }
}

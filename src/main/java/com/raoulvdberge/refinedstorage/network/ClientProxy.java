package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridCraftingPreviewResponseMessage;
import com.raoulvdberge.refinedstorage.network.grid.GridCraftingStartResponseMessage;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.CraftingMonitorScreen;
import com.raoulvdberge.refinedstorage.screen.grid.CraftingPreviewScreen;
import com.raoulvdberge.refinedstorage.screen.grid.CraftingSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientProxy {
    public static void onReceivedCraftingPreviewResponseMessage(GridCraftingPreviewResponseMessage message) {
        Screen screen = Minecraft.getInstance().currentScreen;

        if (screen instanceof CraftingSettingsScreen) {
            screen = ((CraftingSettingsScreen) screen).getParent();
        }

        Minecraft.getInstance().displayGuiScreen(new CraftingPreviewScreen(
            screen,
            message.getFactoryId(),
            message.getStacks(),
            message.getId(),
            message.getQuantity(),
            message.isFluids(),
            new TranslationTextComponent("gui.refinedstorage.crafting_preview")
        ));
    }

    public static void onReceivedCraftingStartResponseMessage(GridCraftingStartResponseMessage message) {
        Screen screen = Minecraft.getInstance().currentScreen;

        if (screen instanceof CraftingSettingsScreen) {
            ((CraftingSettingsScreen) screen).close();
        }
    }

    public static void onReceivedCraftingMonitorUpdateMessage(CraftingMonitorUpdateMessage message) {
        BaseScreen.executeLater(CraftingMonitorScreen.class, craftingMonitor -> craftingMonitor.setTasks(message.getTasks()));
    }
}

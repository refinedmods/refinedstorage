package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorUpdateMessage
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.CraftingMonitorScreen
import com.refinedmods.refinedstorage.screen.grid.CraftingPreviewScreen
import com.refinedmods.refinedstorage.screen.grid.CraftingSettingsScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.TranslationTextComponent

object ClientProxy {
    fun onReceivedCraftingPreviewResponseMessage(message: GridCraftingPreviewResponseMessage) {
        var screen: Screen? = Minecraft.getInstance().currentScreen
        if (screen is CraftingSettingsScreen) {
            screen = (screen as CraftingSettingsScreen).parent
        }
        Minecraft.getInstance().displayGuiScreen(CraftingPreviewScreen(
                screen,
                message.elements,
                message.id,
                message.quantity,
                message.isFluids,
                TranslationTextComponent("gui.refinedstorage.crafting_preview")
        ))
    }

    fun onReceivedCraftingStartResponseMessage() {
        val screen: Screen = Minecraft.getInstance().currentScreen
        if (screen is CraftingSettingsScreen) {
            (screen as CraftingSettingsScreen).close()
        }
    }

    fun onReceivedCraftingMonitorUpdateMessage(message: CraftingMonitorUpdateMessage) {
        BaseScreen.executeLater(CraftingMonitorScreen::class.java) { craftingMonitor: CraftingMonitorScreen -> craftingMonitor.setTasks(message.tasks) }
    }
}
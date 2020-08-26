package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.RSKeyBindings
import com.refinedmods.refinedstorage.network.OpenNetworkItemMessage
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Util
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.*
import java.util.function.Consumer

class KeyInputListener {
    @SubscribeEvent
    fun onKeyInput(e: InputEvent.KeyInputEvent?) {
        if (Minecraft.getInstance().player != null) {
            val inv: PlayerInventory = Minecraft.getInstance().player.inventory
            if (RSKeyBindings.OPEN_WIRELESS_GRID.isKeyDown()) {
                findAndOpen(inv, Consumer<Text?> { error: Text? -> Minecraft.getInstance().player.sendMessage(error, Util.DUMMY_UUID) }, RSItems.WIRELESS_GRID, RSItems.CREATIVE_WIRELESS_GRID)
            } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isKeyDown()) {
                findAndOpen(inv, Consumer<Text?> { error: Text? -> Minecraft.getInstance().player.sendMessage(error, Util.DUMMY_UUID) }, RSItems.WIRELESS_FLUID_GRID, RSItems.CREATIVE_WIRELESS_FLUID_GRID)
            } else if (RSKeyBindings.OPEN_PORTABLE_GRID.isKeyDown()) {
                findAndOpen(inv, Consumer<Text?> { error: Text? -> Minecraft.getInstance().player.sendMessage(error, Util.DUMMY_UUID) }, RSItems.PORTABLE_GRID, RSItems.CREATIVE_PORTABLE_GRID)
            } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isKeyDown()) {
                findAndOpen(inv, Consumer<Text?> { error: Text? -> Minecraft.getInstance().player.sendMessage(error, Util.DUMMY_UUID) }, RSItems.WIRELESS_CRAFTING_MONITOR, RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR)
            }
        }
    }

    companion object {
        fun findAndOpen(inv: IInventory, onError: Consumer<Text?>, vararg items: Item) {
            val validItems: Set<Item> = HashSet(Arrays.asList(*items))
            var slotFound = -1
            for (i in 0 until inv.getSizeInventory()) {
                val slot: ItemStack = inv.getStackInSlot(i)
                if (validItems.contains(slot.item)) {
                    if (slotFound != -1) {
                        onError.accept(TranslationTextComponent("misc.refinedstorage.network_item.shortcut_duplicate", TranslationTextComponent(items[0].translationKey)))
                        return
                    }
                    slotFound = i
                }
            }
            if (slotFound == -1) {
                onError.accept(TranslationTextComponent("misc.refinedstorage.network_item.shortcut_not_found", TranslationTextComponent(items[0].translationKey)))
            } else {
                RS.NETWORK_HANDLER.sendToServer(OpenNetworkItemMessage(slotFound))
            }
        }
    }
}
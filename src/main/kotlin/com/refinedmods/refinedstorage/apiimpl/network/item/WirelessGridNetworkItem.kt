package com.refinedmods.refinedstorage.apiimpl.network.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.item.INetworkItem
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.WirelessGridGridFactory
import com.refinedmods.refinedstorage.item.WirelessGridItem
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage


class WirelessGridNetworkItem(private val handler: INetworkItemManager, override val player: PlayerEntity, private val stack: ItemStack, private val slotId: Int) : INetworkItem {
    override fun onOpen(network: INetwork?): Boolean {
        val energy: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null)
        if (RS.SERVER_CONFIG.wirelessGrid.useEnergy && (stack.item as WirelessGridItem).type != WirelessGridItem.Type.CREATIVE && energy != null && energy.getEnergyStored() <= RS.SERVER_CONFIG.wirelessGrid.openUsage) {
            sendOutOfEnergyMessage()
            return false
        }
        if (!network!!.securityManager!!.hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player)
            return false
        }
        instance().getGridManager()!!.openGrid(WirelessGridGridFactory.Companion.ID, player as ServerPlayerEntity, stack, slotId)
        drainEnergy(RS.SERVER_CONFIG.wirelessGrid.openUsage)
        return true
    }

    override fun drainEnergy(energy: Int) {
        if (RS.SERVER_CONFIG.wirelessGrid.useEnergy && (stack.item as WirelessGridItem).type != WirelessGridItem.Type.CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent({ energyStorage ->
                energyStorage.extractEnergy(energy, false)
                if (energyStorage.getEnergyStored() <= 0) {
                    handler.close(player)
                    player.closeScreen()
                    sendOutOfEnergyMessage()
                }
            })
        }
    }

    private fun sendOutOfEnergyMessage() {
        player.sendMessage(TranslationTextComponent("misc.refinedstorage.network_item.out_of_energy", TranslationTextComponent(stack.item.translationKey)), player.getUniqueID())
    }
}
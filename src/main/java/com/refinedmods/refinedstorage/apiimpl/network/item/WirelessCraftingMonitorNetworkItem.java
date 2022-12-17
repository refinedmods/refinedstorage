package com.refinedmods.refinedstorage.apiimpl.network.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.WirelessCraftingMonitor;
import com.refinedmods.refinedstorage.container.factory.CraftingMonitorMenuProvider;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkHooks;

public class WirelessCraftingMonitorNetworkItem implements INetworkItem {
    private final INetworkItemManager handler;
    private final Player player;
    private final ItemStack stack;
    private final PlayerSlot slot;

    public WirelessCraftingMonitorNetworkItem(INetworkItemManager handler, Player player, ItemStack stack, PlayerSlot slot) {
        this.handler = handler;
        this.player = player;
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean onOpen(INetwork network) {
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY, null).orElse(null);

        if (RS.SERVER_CONFIG.getWirelessCraftingMonitor().getUseEnergy() &&
            ((WirelessCraftingMonitorItem) stack.getItem()).getType() != WirelessCraftingMonitorItem.Type.CREATIVE &&
            energy != null &&
            energy.getEnergyStored() <= RS.SERVER_CONFIG.getWirelessCraftingMonitor().getOpenUsage()) {
            sendOutOfEnergyMessage();

            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player) ||
            !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            LevelUtils.sendNoPermissionMessage(player);

            return false;
        }

        WirelessCraftingMonitor wirelessCraftingMonitor = new WirelessCraftingMonitor(stack, player.getServer(), slot);

        NetworkHooks.openScreen(
            (ServerPlayer) player,
            new CraftingMonitorMenuProvider(RSContainerMenus.WIRELESS_CRAFTING_MONITOR.get(), wirelessCraftingMonitor, null),
            slot::writePlayerSlot
        );

        drainEnergy(RS.SERVER_CONFIG.getWirelessCraftingMonitor().getOpenUsage());

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.SERVER_CONFIG.getWirelessCraftingMonitor().getUseEnergy() && ((WirelessCraftingMonitorItem) stack.getItem()).getType() != WirelessCraftingMonitorItem.Type.CREATIVE) {
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                energyStorage.extractEnergy(energy, false);

                if (energyStorage.getEnergyStored() <= 0) {
                    handler.close(player);

                    player.closeContainer();

                    sendOutOfEnergyMessage();
                }
            });
        }
    }

    private void sendOutOfEnergyMessage() {
        player.sendSystemMessage(Component.translatable("misc.refinedstorage.network_item.out_of_energy", Component.translatable(stack.getItem().getDescriptionId())));
    }
}
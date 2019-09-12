package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessCraftingMonitor implements INetworkItem {
    private INetworkItemHandler handler;
    private PlayerEntity player;
    private ItemStack stack;
    private int invIndex;

    public NetworkItemWirelessCraftingMonitor(INetworkItemHandler handler, PlayerEntity player, ItemStack stack, int invIndex) {
        this.handler = handler;
        this.player = player;
        this.stack = stack;
        this.invIndex = invIndex;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public boolean onOpen(INetwork network) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE */ && energy != null && energy.getEnergyStored() <= RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player) || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        // TODO player.openGui(RS.INSTANCE, RSGui.WIRELESS_CRAFTING_MONITOR, player.getEntityWorld(), invIndex, 0, 0);

        drainEnergy(RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage);

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE*/) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> {
                energyStorage.extractEnergy(energy, false);

                if (energyStorage.getEnergyStored() <= 0) {
                    handler.close(player);

                    player.closeScreen();
                }
            });
        }
    }
}
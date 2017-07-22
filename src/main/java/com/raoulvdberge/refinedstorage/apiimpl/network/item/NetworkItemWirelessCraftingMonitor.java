package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.item.NetworkItemAction;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessCraftingMonitor implements INetworkItem {
    private INetworkItemHandler handler;
    private EntityPlayer player;
    private ItemStack stack;

    public NetworkItemWirelessCraftingMonitor(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        this.handler = handler;
        this.player = player;
        this.stack = stack;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean onOpen(INetwork network, EntityPlayer player, EnumHand hand) {
        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player) || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        player.openGui(RS.INSTANCE, RSGui.WIRELESS_CRAFTING_MONITOR, player.getEntityWorld(), hand.ordinal(), network.world().provider.getDimension(), 0);

        network.sendCraftingMonitorUpdate((EntityPlayerMP) player);

        drainEnergy(RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage);

        return true;
    }

    @Override
    public void onAction(NetworkItemAction action) {
        switch (action) {
            case CRAFTING_TASK_CANCELLED:
                drainEnergy(RS.INSTANCE.config.wirelessCraftingMonitorCancelUsage);
                break;
            case CRAFTING_TASK_ALL_CANCELLED:
                drainEnergy(RS.INSTANCE.config.wirelessCraftingMonitorCancelAllUsage);
                break;
        }
    }

    private void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE) {
            IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

            energyStorage.extractEnergy(energy, false);

            if (energyStorage.getEnergyStored() <= 0) {
                handler.onClose(player);

                player.closeScreen();
            }
        }
    }
}
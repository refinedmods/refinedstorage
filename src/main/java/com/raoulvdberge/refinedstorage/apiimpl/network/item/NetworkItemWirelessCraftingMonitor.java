package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

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
    public boolean onOpen(INetworkMaster network, EntityPlayer player, World controllerWorld, EnumHand hand) {
        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE && RSItems.WIRELESS_CRAFTING_MONITOR.getEnergyStored(stack) <= RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage) {
            return false;
        }

        player.openGui(RS.INSTANCE, RSGui.WIRELESS_CRAFTING_MONITOR, player.getEntityWorld(), hand.ordinal(), controllerWorld.provider.getDimension(), 0);

        network.sendCraftingMonitorUpdate((EntityPlayerMP) player);

        drainEnergy(RS.INSTANCE.config.wirelessCraftingMonitorOpenUsage);

        return true;
    }

    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessCraftingMonitorUsesEnergy && stack.getItemDamage() != ItemWirelessCraftingMonitor.TYPE_CREATIVE) {
            ItemWirelessGrid item = RSItems.WIRELESS_GRID;

            item.extractEnergy(stack, energy, false);

            if (item.getEnergyStored(stack) <= 0) {
                handler.onClose(player);

                player.closeScreen();
            }
        }
    }
}
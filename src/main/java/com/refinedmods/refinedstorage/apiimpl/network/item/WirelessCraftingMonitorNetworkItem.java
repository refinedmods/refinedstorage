package com.refinedmods.refinedstorage.apiimpl.network.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.factory.CraftingMonitorContainerProvider;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem;
import com.refinedmods.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkHooks;

public class WirelessCraftingMonitorNetworkItem implements INetworkItem {
    private final INetworkItemManager handler;
    private final PlayerEntity player;
    private final ItemStack stack;
    private final PlayerSlot slot;

    public WirelessCraftingMonitorNetworkItem(INetworkItemManager handler, PlayerEntity player, ItemStack stack, PlayerSlot slot) {
        this.handler = handler;
        this.player = player;
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public boolean onOpen(INetwork network) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

        if (RS.SERVER_CONFIG.getWirelessCraftingMonitor().getUseEnergy() &&
            ((WirelessCraftingMonitorItem) stack.getItem()).getType() != WirelessCraftingMonitorItem.Type.CREATIVE &&
            energy != null &&
            energy.getEnergyStored() <= RS.SERVER_CONFIG.getWirelessCraftingMonitor().getOpenUsage()) {
            sendOutOfEnergyMessage();

            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player) ||
            !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        WirelessCraftingMonitor wirelessCraftingMonitor = new WirelessCraftingMonitor(stack, player.getServer(), slot);

        NetworkHooks.openGui(
            (ServerPlayerEntity) player,
            new CraftingMonitorContainerProvider(RSContainers.WIRELESS_CRAFTING_MONITOR, wirelessCraftingMonitor, null), slot::writePlayerSlot);

        drainEnergy(RS.SERVER_CONFIG.getWirelessCraftingMonitor().getOpenUsage());

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.SERVER_CONFIG.getWirelessCraftingMonitor().getUseEnergy() && ((WirelessCraftingMonitorItem) stack.getItem()).getType() != WirelessCraftingMonitorItem.Type.CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
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
        player.sendMessage(new TranslationTextComponent("misc.refinedstorage.network_item.out_of_energy", new TranslationTextComponent(stack.getItem().getDescriptionId())), player.getUUID());
    }
}
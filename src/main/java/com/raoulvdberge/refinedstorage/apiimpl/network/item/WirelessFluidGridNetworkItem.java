package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.factory.WirelessFluidGridGridFactory;
import com.raoulvdberge.refinedstorage.item.WirelessFluidGridItem;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class WirelessFluidGridNetworkItem implements INetworkItem {
    private INetworkItemManager handler;
    private PlayerEntity player;
    private ItemStack stack;

    public WirelessFluidGridNetworkItem(INetworkItemManager handler, PlayerEntity player, ItemStack stack) {
        this.handler = handler;
        this.player = player;
        this.stack = stack;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public boolean onOpen(INetwork network) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

        if (RS.SERVER_CONFIG.getWirelessFluidGrid().getUseEnergy() &&
            ((WirelessFluidGridItem) stack.getItem()).getType() != WirelessFluidGridItem.Type.CREATIVE &&
            energy != null &&
            energy.getEnergyStored() <= RS.SERVER_CONFIG.getWirelessFluidGrid().getOpenUsage()) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        API.instance().getGridManager().openGrid(WirelessFluidGridGridFactory.ID, (ServerPlayerEntity) player, stack);

        drainEnergy(RS.SERVER_CONFIG.getWirelessFluidGrid().getOpenUsage());

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.SERVER_CONFIG.getWirelessFluidGrid().getUseEnergy() && ((WirelessFluidGridItem) stack.getItem()).getType() != WirelessFluidGridItem.Type.CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
                energyStorage.extractEnergy(energy, false);

                if (energyStorage.getEnergyStored() <= 0) {
                    handler.close(player);

                    player.closeScreen();
                }
            });
        }
    }
}
package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessGrid implements INetworkItem {
    private INetworkItemHandler handler;
    private PlayerEntity player;
    private ItemStack stack;

    public NetworkItemWirelessGrid(INetworkItemHandler handler, PlayerEntity player, ItemStack stack) {
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

        if (RS.INSTANCE.config.wirelessGridUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE */ && energy != null && energy.getEnergyStored() <= RS.INSTANCE.config.wirelessGridOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        API.instance().getGridManager().openGrid(WirelessGrid.ID, (ServerPlayerEntity) player, stack);

        drainEnergy(RS.INSTANCE.config.wirelessGridOpenUsage);

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessGridUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE*/) {
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
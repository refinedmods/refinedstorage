package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessFluidGrid implements INetworkItem {
    private INetworkItemManager handler;
    private PlayerEntity player;
    private ItemStack stack;

    public NetworkItemWirelessFluidGrid(INetworkItemManager handler, PlayerEntity player, ItemStack stack) {
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

        if (RS.INSTANCE.config.wirelessFluidGridUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessFluidGrid.TYPE_CREATIVE*/ && energy != null && energy.getEnergyStored() <= RS.INSTANCE.config.wirelessFluidGridOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        // TODO API.instance().getGridManager().openGrid(WirelessFluidGrid.ID, (ServerPlayerEntity) player, stack);

        drainEnergy(RS.INSTANCE.config.wirelessFluidGridOpenUsage);

        return true;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessFluidGridUsesEnergy /* TODO && stack.getItemDamage() != ItemWirelessFluidGrid.TYPE_CREATIVE*/) {
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
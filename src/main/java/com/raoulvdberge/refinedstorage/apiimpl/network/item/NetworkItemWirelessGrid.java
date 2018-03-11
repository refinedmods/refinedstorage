package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.item.NetworkItemAction;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessGrid implements INetworkItem {
    private INetworkItemHandler handler;
    private EntityPlayer player;
    private ItemStack stack;

    public NetworkItemWirelessGrid(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
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
        if (RS.INSTANCE.config.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.wirelessGridOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        API.instance().openWirelessGrid(player, hand, network.world().provider.getDimension(), WirelessGrid.ID);

        drainEnergy(RS.INSTANCE.config.wirelessGridOpenUsage);

        return true;
    }

    @Override
    public void onAction(NetworkItemAction action) {
        switch (action) {
            case ITEM_INSERTED:
                drainEnergy(RS.INSTANCE.config.wirelessGridInsertUsage);
                break;
            case ITEM_EXTRACTED:
                drainEnergy(RS.INSTANCE.config.wirelessGridExtractUsage);
                break;
        }
    }

    private void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
            IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

            energyStorage.extractEnergy(energy, false);

            if (energyStorage.getEnergyStored() <= 0) {
                handler.onClose(player);

                player.closeScreen();
            }
        }
    }
}
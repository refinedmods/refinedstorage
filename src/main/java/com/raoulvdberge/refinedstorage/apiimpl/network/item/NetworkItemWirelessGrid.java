package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
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
    public boolean onOpen(INetwork network, EntityPlayer player, World controllerWorld, EnumHand hand) {
        if (RS.INSTANCE.config.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.wirelessGridOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            RSUtils.sendNoPermissionMessage(player);

            return false;
        }

        player.openGui(RS.INSTANCE, RSGui.WIRELESS_GRID, player.getEntityWorld(), hand.ordinal(), controllerWorld.provider.getDimension(), WirelessGrid.GRID_TYPE);

        network.sendItemStorageToClient((EntityPlayerMP) player);

        drainEnergy(RS.INSTANCE.config.wirelessGridOpenUsage);

        return true;
    }

    public void drainEnergy(int energy) {
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
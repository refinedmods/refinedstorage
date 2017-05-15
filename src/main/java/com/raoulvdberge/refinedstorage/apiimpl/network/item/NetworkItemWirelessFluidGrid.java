package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.item.ItemWirelessFluidGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessFluidGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class NetworkItemWirelessFluidGrid implements INetworkItem {
    private INetworkItemHandler handler;
    private EntityPlayer player;
    private ItemStack stack;

    public NetworkItemWirelessFluidGrid(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
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
        if (RS.INSTANCE.config.wirelessFluidGridUsesEnergy && stack.getItemDamage() != ItemWirelessFluidGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.wirelessFluidGridOpenUsage) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            RSUtils.sendNoPermissionMessage(player);

            return false;
        }

        player.openGui(RS.INSTANCE, RSGui.WIRELESS_GRID, player.getEntityWorld(), hand.ordinal(), controllerWorld.provider.getDimension(), WirelessFluidGrid.GRID_TYPE);

        network.sendFluidStorageToClient((EntityPlayerMP) player);

        drainEnergy(RS.INSTANCE.config.wirelessFluidGridOpenUsage);

        return true;
    }

    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessFluidGridUsesEnergy && stack.getItemDamage() != ItemWirelessFluidGrid.TYPE_CREATIVE) {
            IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);

            energyStorage.extractEnergy(energy, false);

            if (energyStorage.getEnergyStored() <= 0) {
                handler.onClose(player);

                player.closeScreen();
            }
        }
    }
}
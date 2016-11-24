package com.raoulvdberge.refinedstorage.apiimpl.network.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

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
    public boolean onOpen(INetworkMaster network, EntityPlayer player, World controllerWorld, EnumHand hand) {
        if (RS.INSTANCE.config.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE && RSItems.WIRELESS_GRID.getEnergyStored(stack) <= RS.INSTANCE.config.wirelessGridOpenUsage) {
            return false;
        }

        player.openGui(RS.INSTANCE, RSGui.WIRELESS_GRID, player.getEntityWorld(), hand.ordinal(), controllerWorld.provider.getDimension(), 0);

        network.sendItemStorageToClient((EntityPlayerMP) player);

        drainEnergy(RS.INSTANCE.config.wirelessGridOpenUsage);

        return true;
    }

    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
            ItemWirelessGrid item = RSItems.WIRELESS_GRID;

            item.extractEnergy(stack, energy, false);

            if (item.getEnergyStored(stack) <= 0) {
                handler.onClose(player);

                player.closeScreen();
            }
        }
    }
}
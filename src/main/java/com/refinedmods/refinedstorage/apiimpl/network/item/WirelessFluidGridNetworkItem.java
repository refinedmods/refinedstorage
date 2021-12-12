package com.refinedmods.refinedstorage.apiimpl.network.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.WirelessFluidGridGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessFluidGridItem;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class WirelessFluidGridNetworkItem implements INetworkItem {
    private final INetworkItemManager handler;
    private final PlayerEntity player;
    private final ItemStack stack;
    private final PlayerSlot slot;

    public WirelessFluidGridNetworkItem(INetworkItemManager handler, PlayerEntity player, ItemStack stack, PlayerSlot slot) {
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

        if (RS.SERVER_CONFIG.getWirelessFluidGrid().getUseEnergy() &&
            ((WirelessFluidGridItem) stack.getItem()).getType() != WirelessFluidGridItem.Type.CREATIVE &&
            energy != null &&
            energy.getEnergyStored() <= RS.SERVER_CONFIG.getWirelessFluidGrid().getOpenUsage()) {
            sendOutOfEnergyMessage();

            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.MODIFY, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return false;
        }

        API.instance().getGridManager().openGrid(WirelessFluidGridGridFactory.ID, (ServerPlayerEntity) player, stack, slot);

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
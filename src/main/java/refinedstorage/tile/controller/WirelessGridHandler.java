package refinedstorage.tile.controller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.storagenet.StorageNetwork;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.tile.grid.WirelessGridConsumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WirelessGridHandler {
    private StorageNetwork network;

    private List<WirelessGridConsumer> consumers = new ArrayList<WirelessGridConsumer>();
    private List<WirelessGridConsumer> consumersToRemove = new ArrayList<WirelessGridConsumer>();

    public WirelessGridHandler(StorageNetwork network) {
        this.network = network;
    }

    public void update() {
        consumers.removeAll(consumersToRemove);
        consumersToRemove.clear();

        Iterator<WirelessGridConsumer> it = consumers.iterator();

        while (it.hasNext()) {
            WirelessGridConsumer consumer = it.next();

            if (!RefinedStorageUtils.compareStack(consumer.getWirelessGrid(), consumer.getPlayer().getHeldItem(consumer.getHand()))) {
                consumer.getPlayer().closeScreen(); // This will call onContainerClosed on the Container and remove it from the list
            }
        }
    }

    public boolean handleOpen(EntityPlayer player, EnumHand hand) {
        int distance = (int) Math.sqrt(Math.pow(network.getPos().getX() - player.posX, 2) + Math.pow(network.getPos().getY() - player.posY, 2) + Math.pow(network.getPos().getZ() - player.posZ, 2));

        if (distance > network.getWirelessGridRange()) {
            return false;
        }

        consumers.add(new WirelessGridConsumer(player, hand, player.getHeldItem(hand)));

        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_GRID, player.worldObj, RefinedStorageUtils.getIdFromHand(hand), 0, 0);

        network.syncItemsWithClient((EntityPlayerMP) player);

        drainEnergy(player, ItemWirelessGrid.USAGE_OPEN);

        return true;
    }

    public void handleClose(EntityPlayer player) {
        WirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            consumersToRemove.add(consumer);
        }
    }

    public void drainEnergy(EntityPlayer player, int energy) {
        WirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            ItemWirelessGrid item = RefinedStorageItems.WIRELESS_GRID;
            ItemStack held = consumer.getPlayer().getHeldItem(consumer.getHand());

            if (held.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
                item.extractEnergy(held, energy, false);

                if (item.getEnergyStored(held) <= 0) {
                    handleClose(player);
                    consumer.getPlayer().closeScreen();
                }
            }
        }
    }

    public WirelessGridConsumer getConsumer(EntityPlayer player) {
        Iterator<WirelessGridConsumer> it = consumers.iterator();

        while (it.hasNext()) {
            WirelessGridConsumer consumer = it.next();

            if (consumer.getPlayer() == player) {
                return consumer;
            }
        }

        return null;
    }

    public List<WirelessGridConsumer> getConsumers() {
        return consumers;
    }
}

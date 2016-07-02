package refinedstorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.IWirelessGridHandler;
import refinedstorage.api.network.WirelessGridConsumer;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.tile.TileWirelessTransmitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WirelessGridHandler implements IWirelessGridHandler {
    public static final int USAGE_OPEN = 30;
    public static final int USAGE_EXTRACT = 3;
    public static final int USAGE_INSERT = 3;

    private INetworkMaster network;

    private List<WirelessGridConsumer> consumers = new ArrayList<WirelessGridConsumer>();
    private List<WirelessGridConsumer> consumersToRemove = new ArrayList<WirelessGridConsumer>();

    public WirelessGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void update() {
        consumers.removeAll(consumersToRemove);
        consumersToRemove.clear();

        Iterator<WirelessGridConsumer> it = consumers.iterator();

        while (it.hasNext()) {
            WirelessGridConsumer consumer = it.next();

            if (!RefinedStorageUtils.compareStack(consumer.getWirelessGrid(), consumer.getPlayer().getHeldItem(consumer.getHand()))) {
                /**
                 * This will call {@link net.minecraft.inventory.Container#onContainerClosed(EntityPlayer)} so the consumer is removed from the list.
                 */
                consumer.getPlayer().closeScreen();
            }
        }
    }

    @Override
    public boolean onOpen(EntityPlayer player, EnumHand hand) {
        int distance = (int) Math.sqrt(Math.pow(network.getPosition().getX() - player.posX, 2) + Math.pow(network.getPosition().getY() - player.posY, 2) + Math.pow(network.getPosition().getZ() - player.posZ, 2));

        if (distance > getRange()) {
            return false;
        }

        consumers.add(new WirelessGridConsumer(player, hand, player.getHeldItem(hand)));

        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_GRID, player.worldObj, RefinedStorageUtils.getIdFromHand(hand), 0, 0);

        network.sendStorageToClient((EntityPlayerMP) player);

        drainEnergy(player, USAGE_OPEN);

        return true;
    }

    @Override
    public void onClose(EntityPlayer player) {
        WirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            consumersToRemove.add(consumer);
        }
    }

    @Override
    public int getRange() {
        int range = 0;

        for (INetworkNode node : network.getNodes()) {
            if (node instanceof TileWirelessTransmitter) {
                range += ((TileWirelessTransmitter) node).getRange();
            }
        }

        return range;
    }

    @Override
    public void drainEnergy(EntityPlayer player, int energy) {
        WirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            ItemWirelessGrid item = RefinedStorageItems.WIRELESS_GRID;
            ItemStack held = consumer.getPlayer().getHeldItem(consumer.getHand());

            if (held.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
                item.extractEnergy(held, energy, false);

                if (item.getEnergyStored(held) <= 0) {
                    onClose(player);
                    consumer.getPlayer().closeScreen();
                }
            }
        }
    }

    @Override
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
}

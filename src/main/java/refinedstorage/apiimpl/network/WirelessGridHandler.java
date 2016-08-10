package refinedstorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.network.*;
import refinedstorage.item.ItemWirelessGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WirelessGridHandler implements IWirelessGridHandler {
    public static final int USAGE_OPEN = 30;
    public static final int USAGE_EXTRACT = 3;
    public static final int USAGE_INSERT = 3;

    private INetworkMaster network;

    private List<IWirelessGridConsumer> consumers = new ArrayList<>();
    private List<IWirelessGridConsumer> consumersToRemove = new ArrayList<>();

    public WirelessGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void update() {
        consumers.removeAll(consumersToRemove);
        consumersToRemove.clear();
    }

    @Override
    public boolean onOpen(EntityPlayer player, EnumHand hand) {
        boolean inRange = false;

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof IWirelessTransmitter) {
                IWirelessTransmitter transmitter = (IWirelessTransmitter) node;

                double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - player.posX, 2) + Math.pow(transmitter.getOrigin().getY() - player.posY, 2) + Math.pow(transmitter.getOrigin().getZ() - player.posZ, 2));

                if (distance < transmitter.getRange()) {
                    inRange = true;

                    break;
                }
            }
        }

        if (!inRange) {
            return false;
        }

        consumers.add(new WirelessGridConsumer(player, player.getHeldItem(hand)));

        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_GRID, player.worldObj, hand.ordinal(), 0, 0);

        network.sendItemStorageToClient((EntityPlayerMP) player);

        drainEnergy(player, USAGE_OPEN);

        return true;
    }

    @Override
    public void onClose(EntityPlayer player) {
        IWirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            consumersToRemove.add(consumer);
        }
    }

    @Override
    public void drainEnergy(EntityPlayer player, int energy) {
        IWirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            ItemWirelessGrid item = RefinedStorageItems.WIRELESS_GRID;

            if (consumer.getStack().getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
                item.extractEnergy(consumer.getStack(), energy, false);

                if (item.getEnergyStored(consumer.getStack()) <= 0) {
                    onClose(player);

                    consumer.getPlayer().closeScreen();
                }
            }
        }
    }

    @Override
    public IWirelessGridConsumer getConsumer(EntityPlayer player) {
        Iterator<IWirelessGridConsumer> it = consumers.iterator();

        while (it.hasNext()) {
            IWirelessGridConsumer consumer = it.next();

            if (consumer.getPlayer() == player) {
                return consumer;
            }
        }

        return null;
    }
}

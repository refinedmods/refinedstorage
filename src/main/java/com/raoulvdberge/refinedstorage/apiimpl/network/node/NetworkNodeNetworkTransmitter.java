package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemNetworkCard;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

public class NetworkNodeNetworkTransmitter extends NetworkNode {
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(1, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_INTERDIMENSIONAL) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (network != null) {
                network.getNodeGraph().rebuild();
            }
        }
    };

    private ItemHandlerBasic networkCard = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this), new ItemValidatorBasic(RSItems.NETWORK_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack card = getStackInSlot(slot);

            if (card.isEmpty()) {
                receiver = null;
            } else {
                receiver = ItemNetworkCard.getReceiver(card);
                receiverDimension = ItemNetworkCard.getDimension(card);
            }

            if (network != null) {
                network.getNodeGraph().rebuild();
            }
        }
    };

    private BlockPos receiver;
    private int receiverDimension;

    public NetworkNodeNetworkTransmitter(INetworkNodeHolder holder) {
        super(holder);

        rebuildOnUpdateChange = true;
    }

    public boolean canTransmit() {
        return canUpdate() && receiver != null && isDimensionSupported();
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(networkCard, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(networkCard, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public int getEnergyUsage() {
        return Math.min(
                RS.INSTANCE.config.interdimensionalUpgradeUsage,
                RS.INSTANCE.config.networkTransmitterUsage + (isSameDimension() ? (int) Math.ceil(RS.INSTANCE.config.networkTransmitterPerBlockUsage * getDistance()) : 0) + upgrades.getEnergyUsage()
        );
    }

    public ItemHandlerBasic getNetworkCard() {
        return networkCard;
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(networkCard, upgrades);
    }

    @Nullable
    public BlockPos getReceiver() {
        return receiver;
    }

    public int getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (receiver == null) {
            return 0;
        }

        return (int) Math.sqrt(Math.pow(holder.pos().getX() - receiver.getX(), 2) + Math.pow(holder.pos().getY() - receiver.getY(), 2) + Math.pow(holder.pos().getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return holder.world().provider.getDimension() == receiverDimension;
    }

    public boolean isDimensionSupported() {
        return isSameDimension() || upgrades.hasUpgrade(ItemUpgrade.TYPE_INTERDIMENSIONAL);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void walkNeighborhood(Operator operator) {
        super.walkNeighborhood(operator);

        if (canTransmit()) {
            if (!isSameDimension()) {
                final World dimensionWorld = DimensionManager.getWorld(receiverDimension);
                if (dimensionWorld != null) {
                    operator.apply(dimensionWorld, receiver, null);
                }
            } else {
                operator.apply(holder.world(), receiver, null);
            }
        }
    }
}

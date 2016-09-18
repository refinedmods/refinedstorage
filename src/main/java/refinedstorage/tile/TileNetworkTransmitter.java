package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemNetworkCard;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public class TileNetworkTransmitter extends TileNode {
    public static final TileDataParameter<Integer> DISTANCE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileNetworkTransmitter>() {
        @Override
        public Integer getValue(TileNetworkTransmitter tile) {
            return (tile.receiver != null && tile.isSameDimension()) ? tile.getDistance() : -1;
        }
    });

    public static final TileDataParameter<Integer> RECEIVER_DIMENSION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileNetworkTransmitter>() {
        @Override
        public Integer getValue(TileNetworkTransmitter tile) {
            return tile.receiverDimension;
        }
    });

    public static final TileDataParameter<Boolean> RECEIVER_DIMENSION_SUPPORTED = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileNetworkTransmitter>() {
        @Override
        public Boolean getValue(TileNetworkTransmitter tile) {
            return tile.isDimensionSupported();
        }
    });

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(1, this, ItemUpgrade.TYPE_INTERDIMENSIONAL) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (network != null) {
                NetworkUtils.rebuildGraph(network);
            }
        }
    };

    private ItemHandlerBasic networkCard = new ItemHandlerBasic(1, this, new ItemValidatorBasic(RefinedStorageItems.NETWORK_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack card = getStackInSlot(slot);

            if (card == null) {
                receiver = null;
            } else {
                receiver = ItemNetworkCard.getReceiver(card);
                receiverDimension = ItemNetworkCard.getDimension(card);
            }

            if (network != null) {
                NetworkUtils.rebuildGraph(network);
            }
        }
    };

    private BlockPos receiver;
    private int receiverDimension;

    public TileNetworkTransmitter() {
        dataManager.addWatchedParameter(DISTANCE);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION_SUPPORTED);

        rebuildOnUpdateChange = true;
    }

    @Override
    public void updateNode() {
    }

    public boolean canTransmit() {
        return canUpdate() && receiver != null && isDimensionSupported();
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(networkCard, 0, tag);
        writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(networkCard, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.config.networkTransmitterUsage + (isSameDimension() ? (int) Math.ceil(RefinedStorage.INSTANCE.config.networkTransmitterPerBlockUsage * getDistance()) : 0) + upgrades.getEnergyUsage();
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

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return worldObj.provider.getDimension() == receiverDimension;
    }

    public boolean isDimensionSupported() {
        return isSameDimension() || upgrades.hasUpgrade(ItemUpgrade.TYPE_INTERDIMENSIONAL);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.ContainerNetworkTransmitter;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemNetworkCard;
import refinedstorage.item.ItemUpgrade;

public class TileNetworkTransmitter extends TileNode {
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(1, this, ItemUpgrade.TYPE_INTERDIMENSION);
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
                network.getNodeGraph().rebuild(network.getPosition());
            }
        }
    };

    private BlockPos receiver;
    private int receiverDimension;

    // Used clientside
    private int distance;
    private boolean inSameDimension;

    public TileNetworkTransmitter() {
        rebuildOnUpdateChange = true;
    }

    @Override
    public void updateNode() {
    }

    public boolean canTransmit() {
        return canUpdate() && receiver != null;
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
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt((receiver != null && isInSameDimension()) ? getDistance() : -1);
        buf.writeBoolean(isInSameDimension());
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        distance = buf.readInt();
        inSameDimension = buf.readBoolean();
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.networkTransmitterUsage + (int) Math.ceil(RefinedStorage.INSTANCE.networkTransmitterPerBlockUsage * getDistance());
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerNetworkTransmitter.class;
    }

    public ItemHandlerBasic getNetworkCard() {
        return networkCard;
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    public BlockPos getReceiver() {
        return receiver;
    }

    public int getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (worldObj.isRemote) {
            return distance;
        }

        if (receiver == null) {
            return 0;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isInSameDimension() {
        return worldObj.isRemote ? inSameDimension : worldObj.provider.getDimension() == receiverDimension;
    }
}

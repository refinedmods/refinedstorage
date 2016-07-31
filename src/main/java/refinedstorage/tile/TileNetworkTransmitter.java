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
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemNetworkCard;

public class TileNetworkTransmitter extends TileNode {
    private ItemHandlerBasic networkCard = new ItemHandlerBasic(1, this, new ItemValidatorBasic(RefinedStorageItems.NETWORK_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack card = getStackInSlot(slot);

            if (card == null) {
                receiver = null;
            } else {
                receiver = ItemNetworkCard.getReceiver(card);
            }

            if (network != null) {
                network.rebuildNodes();
            }
        }
    };
    private ItemHandlerBasic upgrade = new ItemHandlerBasic(1, this);

    private BlockPos receiver;

    private boolean couldUpdate;

    @Override
    public void updateNode() {
    }

    public void update() {
        super.update();

        if (network != null && couldUpdate != canUpdate()) {
            couldUpdate = canUpdate();

            network.rebuildNodes();
        }
    }

    @Override
    public boolean canConduct() {
        return canUpdate();
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(networkCard, 0, tag);
        writeItems(upgrade, 1, tag);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(networkCard, 0, tag);
        readItems(upgrade, 1, tag);
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeBoolean(receiver != null);

        if (receiver != null) {
            buf.writeLong(receiver.toLong());
        }
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        if (buf.readBoolean()) {
            receiver = BlockPos.fromLong(buf.readLong());
        } else {
            receiver = null;
        }
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.networkTransmitterUsage + (int) Math.ceil(RefinedStorage.INSTANCE.networkTransmitterPerBlockUsage * getDistance());
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerNetworkTransmitter.class;
    }

    public ItemHandlerBasic getUpgrade() {
        return upgrade;
    }

    public ItemHandlerBasic getNetworkCard() {
        return networkCard;
    }

    public BlockPos getReceiver() {
        return receiver;
    }

    public int getDistance() {
        if (receiver == null) {
            return 0;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.validator.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkNodeNetworkTransmitter extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "network_transmitter");

    private BaseItemHandler networkCard = new BaseItemHandler(1, new NetworkNodeListener(this), new ItemValidatorBasic(RSItems.NETWORK_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack card = getStackInSlot(slot);

            if (card.isEmpty()) {
                receiver = null;
            } else {
                // TODO receiver = ItemNetworkCard.getReceiver(card);
                // TODO receiverDimension = ItemNetworkCard.getDimension(card);
            }

            if (network != null) {
                network.getNodeGraph().invalidate(Action.PERFORM, network.world(), network.getPosition());
            }
        }
    };

    private BlockPos receiver;
    private int receiverDimension;

    public NetworkNodeNetworkTransmitter(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(networkCard, 0, tag);

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(networkCard, 0, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.networkTransmitterUsage;
    }

    public BaseItemHandler getNetworkCard() {
        return networkCard;
    }

    @Override
    public IItemHandler getDrops() {
        return networkCard;
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

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return world.getDimension().getType().getId() == receiverDimension;
    }

    private boolean canTransmit() {
        return canUpdate() && receiver != null;
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        super.visit(operator);

        if (canTransmit()) {
            if (!isSameDimension()) {

// TODO                final World dimensionWorld = DimensionManager.getWorld(receiverDimension);

       //         if (dimensionWorld != null) {
         //           operator.apply(dimensionWorld, receiver, null);
           //     }
            } else {
                operator.apply(world, receiver, null);
            }
        }
    }
}

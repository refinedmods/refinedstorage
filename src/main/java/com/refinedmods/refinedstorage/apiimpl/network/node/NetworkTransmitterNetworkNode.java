package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.NetworkCardItem;
import com.refinedmods.refinedstorage.tile.NetworkReceiverTile;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkTransmitterNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "network_transmitter");

    private final BaseItemHandler networkCard = new BaseItemHandler(1)
        .addValidator(new ItemValidator(RSItems.NETWORK_CARD.get()))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            ItemStack card = handler.getStackInSlot(slot);

            if (card.isEmpty()) {
                receiver = null;
                receiverDimension = null;
            } else {
                receiver = NetworkCardItem.getReceiver(card);
                receiverDimension = NetworkCardItem.getDimension(card);
            }

            if (network != null) {
                network.getNodeGraph().invalidate(Action.PERFORM, network.getWorld(), network.getPosition());
            }
        });

    private BlockPos receiver;
    private RegistryKey<World> receiverDimension;

    public NetworkTransmitterNetworkNode(World world, BlockPos pos) {
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
        return RS.SERVER_CONFIG.getNetworkTransmitter().getUsage();
    }

    public BaseItemHandler getNetworkCard() {
        return networkCard;
    }

    @Override
    public IItemHandler getDrops() {
        return getNetworkCard();
    }

    @Nullable
    public RegistryKey<World> getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (receiver == null || receiverDimension == null || !isSameDimension()) {
            return -1;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return world.dimension() == receiverDimension;
    }

    private boolean canTransmit() {
        return canUpdate() && receiver != null && receiverDimension != null;
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }

    @Override
    public void visit(INetworkNodeVisitor.Operator operator) {
        super.visit(operator);

        if (canTransmit()) {
            if (!isSameDimension()) {
                World dimensionWorld = world.getServer().getLevel(receiverDimension);

                if (dimensionWorld != null && dimensionWorld.getBlockEntity(receiver) instanceof NetworkReceiverTile) {
                    operator.apply(dimensionWorld, receiver, null);
                }
            } else {
                if (world.getBlockEntity(receiver) instanceof NetworkReceiverTile) {
                    operator.apply(world, receiver, null);
                }
            }
        }
    }
}

package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.NetworkCardItem;
import com.refinedmods.refinedstorage.blockentity.NetworkReceiverBlockEntity;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkTransmitterNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "network_transmitter");
    private BlockPos receiver;
    private ResourceKey<Level> receiverDimension;
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
                network.getNodeGraph().invalidate(Action.PERFORM, network.getLevel(), network.getPosition());
            }
        });

    public NetworkTransmitterNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(networkCard, 0, tag);

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
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
    public ResourceKey<Level> getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (receiver == null || receiverDimension == null || !isSameDimension()) {
            return -1;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return level.dimension() == receiverDimension;
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
                Level dimensionWorld = level.getServer().getLevel(receiverDimension);

                if (dimensionWorld != null && dimensionWorld.getBlockEntity(receiver) instanceof NetworkReceiverBlockEntity) {
                    operator.apply(dimensionWorld, receiver, null);
                }
            } else {
                if (level.getBlockEntity(receiver) instanceof NetworkReceiverBlockEntity) {
                    operator.apply(level, receiver, null);
                }
            }
        }
    }
}

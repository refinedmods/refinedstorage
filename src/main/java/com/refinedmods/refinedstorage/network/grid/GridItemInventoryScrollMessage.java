package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemInventoryScrollMessage {
    private int slot;
    private boolean shift;
    private boolean up;

    public GridItemInventoryScrollMessage(int slot, boolean shift, boolean up) {
        this.slot = slot;
        this.shift = shift;
        this.up = up;
    }

    public static GridItemInventoryScrollMessage decode(PacketBuffer buf) {
        return new GridItemInventoryScrollMessage(buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemInventoryScrollMessage message, PacketBuffer buf) {
        buf.writeInt(message.slot);
        buf.writeBoolean(message.shift);
        buf.writeBoolean(message.up);
    }

    public static void handle(GridItemInventoryScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        int flags = ItemGridHandler.EXTRACT_SINGLE;
                        int slot = message.slot;
                        ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
                        ItemStack stackOnCursor = player.inventory.getItemStack();

                        if (message.shift) { // shift
                            flags |= ItemGridHandler.EXTRACT_SHIFT;
                            if (message.up) { // scroll up
                                player.inventory.setInventorySlotContents(slot, grid.getItemHandler().onInsert(player, stackInSlot, true));
                            } else { // scroll down
                                grid.getItemHandler().onExtract(player, stackInSlot, slot, flags);
                            }

                        } else { //ctrl
                            if (message.up) { // scroll up
                                grid.getItemHandler().onInsert(player, stackOnCursor, true);
                                player.updateHeldItem();
                            } else { //scroll down
                                if (stackOnCursor.isEmpty()) {
                                    grid.getItemHandler().onExtract(player, stackInSlot, -1, flags);
                                } else {
                                    grid.getItemHandler().onExtract(player, stackOnCursor, -1, flags);
                                }

                            }

                        }
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}

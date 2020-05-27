package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridItemGridScrollMessage {

    private UUID id;
    private boolean shift;
    private boolean up;
    private boolean ctrl;


    public GridItemGridScrollMessage(UUID id, boolean shift, boolean ctrl, boolean up) {
        this.id = id;
        this.shift = shift;
        this.ctrl = ctrl;
        this.up = up;
    }

    public static GridItemGridScrollMessage decode(PacketBuffer buf) {
        return new GridItemGridScrollMessage(buf.readUniqueId(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemGridScrollMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeBoolean(message.shift);
        buf.writeBoolean(message.ctrl);
        buf.writeBoolean(message.up);

    }

    public static void handle(GridItemGridScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        int flags = ItemGridHandler.EXTRACT_SINGLE;
                        if (!message.id.equals(new UUID(0, 0))) { //isOverStack
                            if (message.shift && !message.ctrl) { //shift
                                flags |= ItemGridHandler.EXTRACT_SHIFT;
                                if (message.up) { //scroll up
                                    ItemStorageCache cache = (ItemStorageCache) grid.getStorageCache();
                                    if (cache != null) {
                                        ItemStack stack = cache.getList().get(message.id);
                                        if (stack != null) {
                                            int slot = player.inventory.getSlotFor(stack);
                                            if (slot != -1) {
                                                grid.getItemHandler().onInsert(player, player.inventory.getStackInSlot(slot), true);
                                                return;
                                            }
                                        }
                                    }
                                } else { //scroll down
                                    grid.getItemHandler().onExtract(player, message.id, -1, flags);
                                    return;
                                }
                            } else { //ctrl
                                if (!message.up) { //scroll down
                                    grid.getItemHandler().onExtract(player, message.id, -1, flags);
                                    return;
                                }
                            }
                        }
                        if (message.up) { //scroll up
                            grid.getItemHandler().onInsert(player, player.inventory.getItemStack(), true);
                            player.updateHeldItem();
                        }

                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}

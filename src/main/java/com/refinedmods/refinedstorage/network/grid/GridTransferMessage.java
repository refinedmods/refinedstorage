package com.refinedmods.refinedstorage.network.grid;


import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;
public class GridTransferMessage {
    private ItemStack[][] recipe;
    List<List<ItemStack>> inputs;

    public GridTransferMessage() {
    }

    public GridTransferMessage(List<List<ItemStack>> inputs) {
        this.inputs = inputs;
    }

    public static GridTransferMessage decode(FriendlyByteBuf buf) {
        GridTransferMessage msg = new GridTransferMessage();
        int slots = buf.readInt();
        msg.recipe = new ItemStack[slots][];
        for (int i = 0; i < slots; i++) {
            int numberOfIngredients = buf.readInt();
            msg.recipe[i] = new ItemStack[numberOfIngredients];

            for (int j = 0; j < numberOfIngredients; j++) {
                msg.recipe[i][j] = StackUtils.readItemStack(buf);
            }
        }

        return msg;
    }

    public static void encode(GridTransferMessage message, FriendlyByteBuf buf) {

        buf.writeInt(message.inputs.size());
        for (List<ItemStack> stacks : message.inputs) {
            buf.writeInt(stacks.size());

            for (ItemStack possibleStack : stacks) {
                StackUtils.writeItemStack(buf, possibleStack);
            }
        }
    }


    public static void handle(GridTransferMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (player.containerMenu instanceof GridContainerMenu) {
                    IGrid grid = ((GridContainerMenu) player.containerMenu).getGrid();

                    if (grid.getGridType() == GridType.CRAFTING || grid.getGridType() == GridType.PATTERN) {
                        grid.onRecipeTransfer(player, message.recipe);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}

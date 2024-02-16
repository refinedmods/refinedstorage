package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.List;

public class GridTransferMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_transfer");

    private final ItemStack[][] recipe;

    public GridTransferMessage(final ItemStack[][] recipe) {
        this.recipe = recipe;
    }

    public static GridTransferMessage decode(FriendlyByteBuf buf) {
        int slots = buf.readInt();
        final ItemStack[][] recipe = new ItemStack[slots][];
        for (int i = 0; i < slots; i++) {
            int numberOfIngredients = buf.readInt();
            recipe[i] = new ItemStack[numberOfIngredients];
            for (int j = 0; j < numberOfIngredients; j++) {
                recipe[i][j] = StackUtils.readItemStack(buf);
            }
        }
        return new GridTransferMessage(recipe);
    }

    public static void handle(GridTransferMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) player.containerMenu).getGrid();

                if (grid.getGridType() == GridType.CRAFTING || grid.getGridType() == GridType.PATTERN) {
                    grid.onRecipeTransfer(player, message.recipe);
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(recipe.length);
        for (ItemStack[] stacks : recipe) {
            buf.writeInt(stacks.length);
            for (ItemStack possibleStack : stacks) {
                StackUtils.writeItemStack(buf, possibleStack);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

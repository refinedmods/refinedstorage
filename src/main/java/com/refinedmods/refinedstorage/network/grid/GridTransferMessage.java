package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.util.StackUtils;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GridTransferMessage {
    private Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs;
    private List<Slot> slots;

    private final ItemStack[][] recipe = new ItemStack[9][];

    public GridTransferMessage() {
    }

    public GridTransferMessage(Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs, List<Slot> slots) {
        this.inputs = inputs;
        this.slots = slots;
    }

    public static GridTransferMessage decode(PacketBuffer buf) {
        GridTransferMessage msg = new GridTransferMessage();

        int slots = buf.readInt();

        for (int i = 0; i < slots; ++i) {
            int ingredients = buf.readInt();

            msg.recipe[i] = new ItemStack[ingredients];

            for (int j = 0; j < ingredients; ++j) {
                msg.recipe[i][j] = StackUtils.readItemStack(buf);
            }
        }

        return msg;
    }

    public static void encode(GridTransferMessage message, PacketBuffer buf) {
        buf.writeInt(message.slots.size());

        for (Slot slot : message.slots) {
            IGuiIngredient<ItemStack> ingredient = message.inputs.get(slot.getSlotIndex() + 1);

            List<ItemStack> ingredients = new ArrayList<>();

            if (ingredient != null) {
                for (ItemStack possibleStack : ingredient.getAllIngredients()) {
                    if (possibleStack != null) {
                        ingredients.add(possibleStack);
                    }
                }
            }

            buf.writeInt(ingredients.size());

            for (ItemStack possibleStack : ingredients) {
                StackUtils.writeItemStack(buf, possibleStack);
            }
        }
    }

    public static void handle(GridTransferMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (player.containerMenu instanceof GridContainer) {
                    IGrid grid = ((GridContainer) player.containerMenu).getGrid();

                    if (grid.getGridType() == GridType.CRAFTING || grid.getGridType() == GridType.PATTERN) {
                        grid.onRecipeTransfer(player, message.recipe);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}

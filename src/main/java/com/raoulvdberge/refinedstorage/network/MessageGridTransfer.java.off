package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import mezz.jei.api.gui.IGuiIngredient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageGridTransfer extends MessageHandlerPlayerToServer<MessageGridTransfer> implements IMessage {
    private Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs;
    private List<Slot> slots;

    private ItemStack[][] recipe = new ItemStack[9][];

    public MessageGridTransfer() {
    }

    public MessageGridTransfer(Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs, List<Slot> slots) {
        this.inputs = inputs;
        this.slots = slots;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int slots = buf.readInt();

        for (int i = 0; i < slots; ++i) {
            int ingredients = buf.readInt();

            recipe[i] = new ItemStack[ingredients];

            for (int j = 0; j < ingredients; ++j) {
                recipe[i][j] = StackUtils.readItemStack(buf);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slots.size());

        for (Slot slot : slots) {
            IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);

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

    @Override
    public void handle(MessageGridTransfer message, ServerPlayerEntity player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getGridType() == GridType.CRAFTING || grid.getGridType() == GridType.PATTERN) {
                grid.onRecipeTransfer(player, message.recipe);
            }
        }
    }
}

package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.*;
import mezz.jei.api.gui.IGuiIngredient;
import net.minecraft.entity.player.EntityPlayerMP;
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
    private io.netty.buffer.ByteBuf buffer = Unpooled.buffer();
    public MessageGridTransfer() {
    }

    public MessageGridTransfer(Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs, List<Slot> slots) {
        this.inputs = inputs;
        this.slots = slots;
        createBuffer();
    }

    private void createBuffer() {
        buffer.writeInt(slots.size());

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

            buffer.writeInt(ingredients.size());

            for (ItemStack possibleStack : ingredients) {
                StackUtils.writeItemStack(buffer, possibleStack);
            }
        }

        if (buffer.array().length > 32767){
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }
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
     buf.writeBytes(buffer);

    }

    @Override
    public void handle(MessageGridTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getGridType() == GridType.CRAFTING || grid.getGridType() == GridType.PATTERN) {
                grid.onRecipeTransfer(player, message.recipe);
            }
        }
    }
}

package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridTransfer extends MessageHandlerPlayerToServer<MessageGridTransfer> implements IMessage {
    private NBTTagCompound recipe;

    public MessageGridTransfer() {
    }

    public MessageGridTransfer(NBTTagCompound recipe) {
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        recipe = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, recipe);
    }

    @Override
    public void handle(MessageGridTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getType() == GridType.CRAFTING || grid.getType() == GridType.PATTERN) {
                ItemStack[][] actualRecipe = new ItemStack[9][];

                for (int x = 0; x < actualRecipe.length; x++) {
                    NBTTagList list = message.recipe.getTagList("#" + x, Constants.NBT.TAG_COMPOUND);

                    if (list.tagCount() > 0) {
                        actualRecipe[x] = new ItemStack[list.tagCount()];

                        for (int y = 0; y < list.tagCount(); y++) {
                            actualRecipe[x][y] = new ItemStack(list.getCompoundTagAt(y));
                        }
                    }
                }

                grid.onRecipeTransfer(player, actualRecipe);
            }
        }
    }
}

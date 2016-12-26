package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
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

public class MessageGridCraftingTransfer extends MessageHandlerPlayerToServer<MessageGridCraftingTransfer> implements IMessage {
    private NBTTagCompound recipe;

    public MessageGridCraftingTransfer() {
    }

    public MessageGridCraftingTransfer(NBTTagCompound recipe) {
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
    public void handle(MessageGridCraftingTransfer message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) {
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

                ((NetworkNodeGrid) grid).onRecipeTransfer(player, actualRecipe);
            }
        }
    }
}

package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

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

            if (grid instanceof TileGrid) {
                if (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) {
                    ItemStack[][] actualRecipe = new ItemStack[9][];

                    for (int x = 0; x < actualRecipe.length; x++) {
                        NBTTagList list = message.recipe.getTagList("#" + x, Constants.NBT.TAG_COMPOUND);

                        if (list.tagCount() > 0) {
                            actualRecipe[x] = new ItemStack[list.tagCount()];

                            for (int y = 0; y < list.tagCount(); y++) {
                                actualRecipe[x][y] = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(y));
                            }
                        }
                    }

                    ((TileGrid) grid).onRecipeTransfer(actualRecipe);
                }
            }
        }
    }
}

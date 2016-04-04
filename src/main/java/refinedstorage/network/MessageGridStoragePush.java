package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.tile.TileController;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridStoragePush extends MessageHandlerPlayerToServer<MessageGridStoragePush> implements IMessage {
    private int gridX;
    private int gridY;
    private int gridZ;
    private int playerSlot;
    private boolean one;

    public MessageGridStoragePush() {
    }

    public MessageGridStoragePush(int gridX, int gridY, int gridZ, int playerSlot, boolean one) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
        this.playerSlot = playerSlot;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gridX = buf.readInt();
        gridY = buf.readInt();
        gridZ = buf.readInt();
        playerSlot = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gridX);
        buf.writeInt(gridY);
        buf.writeInt(gridZ);
        buf.writeInt(playerSlot);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageGridStoragePush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.gridX, message.gridY, message.gridZ));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            TileController controller = ((TileGrid) tile).getController();

            ItemStack stack;

            if (message.playerSlot == -1) {
                stack = player.inventory.getItemStack().copy();

                if (message.one) {
                    stack.stackSize = 1;
                }
            } else {
                stack = player.inventory.getStackInSlot(message.playerSlot);
            }

            if (stack != null) {
                boolean success = controller.push(stack);

                if (success) {
                    if (message.playerSlot == -1) {
                        if (message.one) {
                            player.inventory.getItemStack().stackSize--;

                            if (player.inventory.getItemStack().stackSize == 0) {
                                player.inventory.setItemStack(null);
                            }
                        } else {
                            player.inventory.setItemStack(null);
                        }

                        player.updateHeldItem();
                    } else {
                        player.inventory.setInventorySlotContents(message.playerSlot, null);
                    }
                }

                controller.drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_PUSH);
            }
        }
    }
}

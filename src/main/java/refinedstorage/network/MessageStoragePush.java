package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.TileController;

public class MessageStoragePush extends MessageHandlerPlayerToServer<MessageStoragePush> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int slot;
    private boolean one;

    public MessageStoragePush() {
    }

    public MessageStoragePush(int x, int y, int z, int slot, boolean one) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slot = slot;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        slot = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(slot);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageStoragePush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileController) {
            TileController controller = (TileController) tile;

            ItemStack stack;

            if (message.slot == -1) {
                stack = player.inventory.getItemStack().copy();

                if (message.one) {
                    stack.stackSize = 1;
                }
            } else {
                stack = player.inventory.getStackInSlot(message.slot);
            }

            if (stack != null) {
                boolean success = controller.push(stack);

                if (success) {
                    if (message.slot == -1) {
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
                        player.inventory.setInventorySlotContents(message.slot, null);
                    }
                }
            }
        }
    }
}

package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.TileController;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridStoragePull extends MessageHandlerPlayerToServer<MessageGridStoragePull> implements IMessage {
    public static final int PULL_HALF = 1;
    public static final int PULL_ONE = 2;
    public static final int PULL_SHIFT = 4;

    private int gridX;
    private int gridY;
    private int gridZ;
    private int id;
    private int flags;

    public MessageGridStoragePull() {
    }

    public MessageGridStoragePull(int gridX, int gridY, int gridZ, int id, int flags) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
        this.id = id;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gridX = buf.readInt();
        gridY = buf.readInt();
        gridZ = buf.readInt();
        id = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gridX);
        buf.writeInt(gridY);
        buf.writeInt(gridZ);
        buf.writeInt(id);
        buf.writeInt(flags);
    }

    public boolean isPullingHalf() {
        return (flags & PULL_HALF) == PULL_HALF;
    }

    public boolean isPullingOne() {
        return (flags & PULL_ONE) == PULL_ONE;
    }

    public boolean isPullingWithShift() {
        return (flags & PULL_SHIFT) == PULL_SHIFT;
    }

    @Override
    public void handle(MessageGridStoragePull message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.gridX, message.gridY, message.gridZ));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            TileController controller = ((TileGrid) tile).getController();

            if (message.id < controller.getItemGroups().size()) {
                ItemGroup group = controller.getItemGroups().get(message.id);

                int quantity = 64;

                if (message.isPullingHalf() && group.getQuantity() > 1) {
                    quantity = group.getQuantity() / 2;

                    if (quantity > 32) {
                        quantity = 32;
                    }
                } else if (message.isPullingOne()) {
                    quantity = 1;
                } else if (message.isPullingWithShift()) {
                    // NO OP, the quantity already set (64) is needed for shift
                }

                if (quantity > group.getType().getItemStackLimit(group.toItemStack())) {
                    quantity = group.getType().getItemStackLimit(group.toItemStack());
                }

                ItemStack took = controller.take(group.copy(quantity).toItemStack());

                if (took != null) {
                    if (message.isPullingWithShift()) {
                        if (!player.inventory.addItemStackToInventory(took.copy())) {
                            controller.push(took);
                        }
                    } else {
                        player.inventory.setItemStack(took);
                        player.updateHeldItem();
                    }

                    controller.drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_PULL);
                }
            }
        }
    }
}

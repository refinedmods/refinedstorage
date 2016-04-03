package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridCraftingPush extends MessageHandlerPlayerToServer<MessageGridCraftingPush> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int craftingSlot;

    public MessageGridCraftingPush() {
    }

    public MessageGridCraftingPush(TileGrid grid, int craftingSlot) {
        this.x = grid.getPos().getX();
        this.y = grid.getPos().getY();
        this.z = grid.getPos().getZ();
        this.craftingSlot = craftingSlot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        craftingSlot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(craftingSlot);
    }

    @Override
    public void handle(MessageGridCraftingPush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid) {
            TileGrid grid = (TileGrid) tile;

            if (grid.isConnected() && grid.getType() == EnumGridType.CRAFTING && message.craftingSlot < grid.getCraftingInventory().getSizeInventory()) {
                ItemStack stack = grid.getCraftingInventory().getStackInSlot(message.craftingSlot);

                if (stack != null) {
                    if (grid.getController().push(stack)) {
                        grid.getCraftingInventory().setInventorySlotContents(message.craftingSlot, null);
                    }
                }
            }
        }
    }
}

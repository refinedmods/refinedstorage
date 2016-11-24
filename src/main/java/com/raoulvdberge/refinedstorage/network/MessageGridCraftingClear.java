package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingClear extends MessageHandlerPlayerToServer<MessageGridCraftingClear> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageGridCraftingClear() {
    }

    public MessageGridCraftingClear(TileGrid grid) {
        this.x = grid.getPos().getX();
        this.y = grid.getPos().getY();
        this.z = grid.getPos().getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(MessageGridCraftingClear message, EntityPlayerMP player) {
        TileEntity tile = player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid) {
            TileGrid grid = (TileGrid) tile;

            if (grid.isConnected()) {
                if (grid.getType() == EnumGridType.CRAFTING) {
                    for (int i = 0; i < grid.getMatrix().getSizeInventory(); ++i) {
                        ItemStack slot = grid.getMatrix().getStackInSlot(i);

                        if (slot != null) {
                            grid.getMatrix().setInventorySlotContents(i, grid.getNetwork().insertItem(slot, slot.stackSize, false));
                        }
                    }
                } else if (grid.getType() == EnumGridType.PATTERN) {
                    for (int i = 0; i < grid.getMatrix().getSizeInventory(); ++i) {
                        grid.getMatrix().setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }
}

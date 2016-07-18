package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageGridSettingsUpdate> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;

    public MessageGridSettingsUpdate() {
    }

    public MessageGridSettingsUpdate(TileGrid grid, int viewType, int sortingDirection, int sortingType, int searchBoxMode) {
        this.x = grid.getPos().getX();
        this.y = grid.getPos().getY();
        this.z = grid.getPos().getZ();
        this.viewType = viewType;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        viewType = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(viewType);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
    }

    @Override
    public void handle(MessageGridSettingsUpdate message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid) {
            if (TileGrid.isValidViewType(message.viewType)) {
                ((TileGrid) tile).setViewType(message.viewType);
            }

            if (TileGrid.isValidSortingDirection(message.sortingDirection)) {
                ((TileGrid) tile).setSortingDirection(message.sortingDirection);
            }

            if (TileGrid.isValidSortingType(message.sortingType)) {
                ((TileGrid) tile).setSortingType(message.sortingType);
            }

            if (TileGrid.isValidSearchBoxMode(message.searchBoxMode)) {
                ((TileGrid) tile).setSearchBoxMode(message.searchBoxMode);
            }
        }
    }
}

package refinedstorage.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.network.MessageTileContainerUpdate;

public abstract class TileBase extends TileEntity implements ITickable {
    public static final String NBT_DIRECTION = "Direction";

    private EnumFacing direction = EnumFacing.NORTH;

    protected int ticks;

    @Override
    public void update() {
        ticks++;

        if (!worldObj.isRemote) {
            if (this instanceof ISynchronizedContainer) {
                for (EntityPlayer player : worldObj.playerEntities) {
                    if (((ISynchronizedContainer) this).getContainer() == player.openContainer.getClass()) {
                        RefinedStorage.NETWORK.sendTo(new MessageTileContainerUpdate(this), (EntityPlayerMP) player);
                    }
                }
            }
        }
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;

        markDirty();
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public NBTTagCompound write(NBTTagCompound tag) {
        tag.setInteger(NBT_DIRECTION, direction.ordinal());

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        tag.setByte("U", (byte) 1);
        tag.setInteger(NBT_DIRECTION, direction.ordinal());

        return tag;
    }

    public void read(NBTTagCompound tag) {
        direction = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));
    }

    public void readUpdate(NBTTagCompound tag) {
        direction = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));

        RefinedStorageUtils.updateBlock(worldObj, pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("U")) {
            readUpdate(nbt);
        } else {
            read(nbt);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return write(super.writeToNBT(tag));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public TileEntity getFacingTile() {
        return worldObj.getTileEntity(pos.offset(direction));
    }

    public IItemHandler getDroppedItems() {
        return null;
    }
}

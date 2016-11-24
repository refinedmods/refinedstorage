package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class TileBase extends TileEntity implements ITickable {
    private static final String NBT_DIRECTION = "Direction";

    private EnumFacing direction = EnumFacing.NORTH;

    protected TileDataManager dataManager = new TileDataManager(this);
    protected int ticks = 0;

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            ticks++;

            dataManager.detectAndSendChanges();
        }
    }

    public void updateBlock() {
        if (getWorld() != null) {
            getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 1 | 2);
        }
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;

        markDirty();
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public TileDataManager getDataManager() {
        return dataManager;
    }

    public NBTTagCompound write(NBTTagCompound tag) {
        tag.setInteger(NBT_DIRECTION, direction.ordinal());

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        tag.setInteger(NBT_DIRECTION, direction.ordinal());

        return tag;
    }

    public void read(NBTTagCompound tag) {
        direction = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));
    }

    public void readUpdate(NBTTagCompound tag) {
        direction = EnumFacing.getFront(tag.getInteger(NBT_DIRECTION));

        updateBlock();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.readFromNBT(tag);

        readUpdate(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        read(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return write(super.writeToNBT(tag));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public TileEntity getFacingTile() {
        return getWorld().getTileEntity(pos.offset(direction));
    }

    public IItemHandler getDrops() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TileBase && ((TileBase) o).getPos().equals(pos) && ((TileBase) o).getWorld().provider.getDimension() == getWorld().provider.getDimension();
    }

    @Override
    public int hashCode() {
        int result = pos.hashCode();
        result = 31 * result + getWorld().provider.getDimension();
        return result;
    }
}

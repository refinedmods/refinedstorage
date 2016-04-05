package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.block.BlockMachine;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

public abstract class TileMachine extends TileBase implements INetworkTile, IRedstoneModeConfig {
    protected boolean connected = false;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected TileController controller;

    public void onConnected(TileController controller) {
        if (worldObj != null && worldObj.getBlockState(pos).getBlock() == getBlockType()) {
            markDirty();

            this.connected = true;
            this.controller = controller;

            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, true));
        }
    }

    public void onDisconnected() {
        if (worldObj != null && worldObj.getBlockState(pos).getBlock() == getBlockType()) {
            markDirty();

            this.connected = false;
            this.controller = null;

            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, false));
        }
    }

    public TileController getController() {
        return controller;
    }

    @Override
    public void update() {
        super.update();

        if (worldObj != null && !worldObj.isRemote && isConnected()) {
            updateMachine();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        markDirty();

        this.redstoneMode = mode;
    }

    @Override
    public BlockPos getMachinePos() {
        return pos;
    }

    @Override
    public void receiveData(ByteBuf buf) {
        boolean lastConnected = connected;

        connected = buf.readBoolean();

        if (lastConnected != connected) {
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2 | 4);
        }
    }

    @Override
    public void sendData(ByteBuf buf) {
        buf.writeBoolean(connected);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        redstoneMode = RedstoneMode.getById(buf.readInt());
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        buf.writeInt(redstoneMode.id);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(RedstoneMode.NBT, redstoneMode.id);
    }

    public abstract int getEnergyUsage();

    public abstract void updateMachine();
}

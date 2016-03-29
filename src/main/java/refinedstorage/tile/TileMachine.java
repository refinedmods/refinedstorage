package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.block.BlockMachine;
import refinedstorage.tile.settings.IRedstoneModeSetting;
import refinedstorage.tile.settings.RedstoneMode;

public abstract class TileMachine extends TileBase implements INetworkTile, IRedstoneModeSetting {
    protected boolean connected = false;

    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private BlockPos controllerPosition;

    private Block originalBlock;

    public void onConnected(TileController controller) {
        if (worldObj != null && worldObj.getBlockState(pos).getBlock() == originalBlock) {
            markDirty();

            connected = true;

            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, true));

            controllerPosition = controller.getPos();
        }
    }

    public void onDisconnected() {
        if (worldObj != null && worldObj.getBlockState(pos).getBlock() == originalBlock) {
            markDirty();

            connected = false;

            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, false));
        }
    }

    @Override
    public void update() {
        if (worldObj == null) {
            super.update();
            return;
        }

        if (ticks == 0) {
            originalBlock = worldObj.getBlockState(pos).getBlock();
        }

        super.update();

        if (!worldObj.isRemote && isConnected()) {
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
    public BlockPos getTilePos() {
        return pos;
    }

    public TileController getController() {
        return (TileController) worldObj.getTileEntity(controllerPosition);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean lastConnected = connected;

        connected = buf.readBoolean();

        if (connected) {
            controllerPosition = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }

        redstoneMode = RedstoneMode.getById(buf.readInt());

        if (lastConnected != connected) {
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2 | 4);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(connected);

        if (connected) {
            buf.writeInt(controllerPosition.getX());
            buf.writeInt(controllerPosition.getY());
            buf.writeInt(controllerPosition.getZ());
        }

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

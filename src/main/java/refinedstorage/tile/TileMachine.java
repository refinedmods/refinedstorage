package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.block.BlockMachine;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.HashSet;
import java.util.Set;

public abstract class TileMachine extends TileBase implements INetworkTile, IRedstoneModeConfig {
    protected boolean connected = false;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected TileController controller;
    private Block block;

    private Set<String> visited = new HashSet<String>();

    public TileController getController() {
        return controller;
    }

    public void searchController() {
        visited.clear();

        TileController newController = ControllerSearcher.search(worldObj, pos, visited);

        if (controller == null) {
            if (newController != null) {
                onConnected(newController);
            }
        } else {
            if (newController == null) {
                onDisconnected();
            }
        }
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && ticks == 0) {
            block = worldObj.getBlockState(pos).getBlock();

            searchController();
        }

        super.update();
    }

    public void onConnected(TileController controller) {
        this.controller = controller;
        this.connected = true;

        if (worldObj.getBlockState(pos).getBlock() == block) {
            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, true));
        }

        worldObj.notifyNeighborsOfStateChange(pos, block);

        controller.addMachine(this);
    }

    public void onDisconnected() {
        this.connected = false;

        if (worldObj.getBlockState(pos).getBlock() == block) {
            worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockMachine.CONNECTED, false));
        }

        // I have no idea why this is needed
        if (controller != null) {
            this.controller.removeMachine(this);
            this.controller = null;
        }

        worldObj.notifyNeighborsOfStateChange(pos, block);
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

package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.network.MessageMachineConnectedUpdate;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.HashSet;
import java.util.Set;

public abstract class TileMachine extends TileBase implements ISynchronizedContainer, IRedstoneModeConfig {
    protected boolean connected = false;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected TileController controller;
    private Block block;

    private Set<String> visited = new HashSet<String>();

    public TileController getController() {
        return controller;
    }

    // We use a world parameter here and not worldObj because in BlockMachine.onNeighborBlockChange
    // this method is called and at that point in time worldObj is not set yet.
    public void searchController(World world) {
        visited.clear();

        TileController newController = ControllerSearcher.search(worldObj, pos, visited);

        if (controller == null) {
            if (newController != null && newController.canRun() && redstoneMode.isEnabled(worldObj, pos)) {
                onConnected(world, newController);
            }
        } else {
            if (newController == null) {
                onDisconnected(world);
            }
        }
    }

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote) {
            if (ticks == 1) {
                block = worldObj.getBlockState(pos).getBlock();

                searchController(worldObj);
            }

            if (connected && !redstoneMode.isEnabled(worldObj, pos)) {
                onDisconnected(worldObj);
            }

            RefinedStorageUtils.sendToAllAround(worldObj, pos, new MessageMachineConnectedUpdate(this));
        }
    }

    public void onConnected(World world, TileController controller) {
        this.controller = controller;
        this.connected = true;

        world.notifyNeighborsOfStateChange(pos, block);

        controller.addMachine(this);
    }

    public void onDisconnected(World world) {
        this.connected = false;

        if (this.controller != null) {
            this.controller.removeMachine(this);
            this.controller = null;
        }

        world.notifyNeighborsOfStateChange(pos, block);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
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

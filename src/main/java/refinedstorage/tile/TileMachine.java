package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.network.MessageMachineConnectedUpdate;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.HashSet;
import java.util.Set;

public abstract class TileMachine extends TileBase implements ISynchronizedContainer, IRedstoneModeConfig {
    public static final String NBT_CONTROLLER_X = "ControllerX";
    public static final String NBT_CONTROLLER_Y = "ControllerY";
    public static final String NBT_CONTROLLER_Z = "ControllerZ";

    protected boolean connected = false;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected TileController controller;
    private Block block;

    // Used for caching
    private boolean controllerIsCached;
    private int controllerX;
    private int controllerY;
    private int controllerZ;

    private Set<String> visited = new HashSet<String>();

    public TileController getController() {
        return controller;
    }

    public void searchController(World world) {
        visited.clear();

        TileController newController = ControllerSearcher.search(world, pos, visited);

        if (controller == null) {
            if (newController != null) {
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

                boolean search = true;

                if (controllerIsCached) {
                    TileEntity tile = worldObj.getTileEntity(new BlockPos(controllerX, controllerY, controllerZ));

                    if (tile instanceof TileController) {
                        search = !tryConnect((TileController) tile);
                    }
                }

                if (search) {
                    searchController(worldObj);
                }
            }

            if (!(this instanceof TileCable)) {
                RefinedStorageUtils.sendToAllAround(worldObj, pos, new MessageMachineConnectedUpdate(this));
            }
        }
    }

    public boolean mayUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public void onConnected(World world, TileController controller) {
        if (tryConnect(controller)) {
            world.notifyNeighborsOfStateChange(pos, block);
        }
    }

    private boolean tryConnect(TileController controller) {
        if (!controller.canRun()) {
            return false;
        }

        this.controller = controller;
        this.connected = true;

        controller.addMachine(this);

        return true;
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

        controllerIsCached = nbt.hasKey(NBT_CONTROLLER_X) && nbt.hasKey(NBT_CONTROLLER_Y) && nbt.hasKey(NBT_CONTROLLER_Z);

        if (controllerIsCached) {
            controllerX = nbt.getInteger(NBT_CONTROLLER_X);
            controllerY = nbt.getInteger(NBT_CONTROLLER_Y);
            controllerZ = nbt.getInteger(NBT_CONTROLLER_Z);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(RedstoneMode.NBT, redstoneMode.id);

        if (connected) {
            nbt.setInteger(NBT_CONTROLLER_X, controller.getPos().getX());
            nbt.setInteger(NBT_CONTROLLER_Y, controller.getPos().getY());
            nbt.setInteger(NBT_CONTROLLER_Z, controller.getPos().getZ());
        } else {
            nbt.removeTag(NBT_CONTROLLER_X);
            nbt.removeTag(NBT_CONTROLLER_Y);
            nbt.removeTag(NBT_CONTROLLER_Z);
        }
    }

    public abstract int getEnergyUsage();

    public abstract void updateMachine();
}

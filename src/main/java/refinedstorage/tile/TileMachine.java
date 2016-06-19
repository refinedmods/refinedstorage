package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.controller.ControllerSearcher;
import refinedstorage.tile.controller.TileController;

import java.util.HashSet;
import java.util.Set;

public abstract class TileMachine extends TileBase implements ISynchronizedContainer, IRedstoneModeConfig {
    public static final String NBT_CONNECTED = "Connected";

    protected boolean connected;
    protected boolean wasConnected;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected TileController controller;

    private Block block;

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
        if (!worldObj.isRemote) {
            if (ticks == 0) {
                block = worldObj.getBlockState(pos).getBlock();

                searchController(worldObj);
            }

            if (wasConnected != isActive() && canSendConnectivityData()) {
                wasConnected = isActive();

                RefinedStorageUtils.updateBlock(worldObj, pos);
            }

            if (isActive()) {
                updateMachine();
            }
        }

        super.update();
    }

    public boolean canSendConnectivityData() {
        return true;
    }

    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return connected && canUpdate();
    }

    public void onConnected(World world, TileController controller) {
        if (tryConnect(controller) && block != null) {
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
    public void readContainerData(ByteBuf buf) {
        redstoneMode = RedstoneMode.getById(buf.readInt());
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        buf.writeInt(redstoneMode.id);
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        if (nbt.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.id);

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_CONNECTED, isActive());

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        connected = tag.getBoolean(NBT_CONNECTED);
    }

    public abstract int getEnergyUsage();

    public abstract void updateMachine();

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TileMachine)) {
            return false;
        }

        return ((TileMachine) other).getPos().equals(pos);
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}

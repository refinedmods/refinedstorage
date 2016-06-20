package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.api.network.NetworkMasterRegistry;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.controller.TileController;

import java.util.HashSet;
import java.util.Set;

public abstract class TileSlave extends TileBase implements ISynchronizedContainer, IRedstoneModeConfig, INetworkSlave {
    public static final String NBT_CONNECTED = "Connected";

    protected boolean connected;
    protected RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected NetworkMaster network;

    private boolean wasActive;
    private Set<String> visited = new HashSet<String>();

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    @Override
    public void updateConnectivity() {
        if (wasActive != isActive()) {
            wasActive = isActive();

            RefinedStorageUtils.updateBlock(worldObj, pos);
        }
    }

    @Override
    public void connect(World world, NetworkMaster network) {
        if (network.canRun()) {
            this.network = network;
            this.connected = true;

            this.network.addSlave(this);

            world.notifyNeighborsOfStateChange(pos, getBlockType());
        }
    }

    @Override
    public void forceConnect(NetworkMaster network) {
        this.network = network;
        this.connected = true;
    }

    @Override
    public void disconnect(World world) {
        this.connected = false;

        if (this.network != null) {
            this.network.removeSlave(this);
            this.network = null;
        }

        world.notifyNeighborsOfStateChange(pos, getBlockType());
    }

    @Override
    public void onNeighborChanged(World world) {
        visited.clear();

        TileController controller = searchController(world, pos, visited);

        if (network == null) {
            if (controller != null) {
                connect(world, NetworkMasterRegistry.get(controller.getPos(), world.provider.getDimension()));
            }
        } else {
            if (controller == null) {
                disconnect(world);
            }
        }
    }

    private TileController searchController(World world, BlockPos current, Set<String> visited) {
        if (visited.contains(current.getX() + "," + current.getY() + "," + current.getZ())) {
            return null;
        }

        visited.add(current.getX() + "," + current.getY() + "," + current.getZ());

        TileEntity tile = world.getTileEntity(current);

        if (tile instanceof TileController) {
            return (TileController) tile;
        } else if (tile instanceof TileSlave) {
            if (visited.size() > 1 && tile instanceof TileRelay && !((TileRelay) tile).canUpdate()) {
                return null;
            }

            for (EnumFacing dir : EnumFacing.VALUES) {
                TileController controller = searchController(world, current.offset(dir), visited);

                if (controller != null) {
                    return controller;
                }
            }
        }

        return null;
    }

    public NetworkMaster getNetwork() {
        return network;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TileSlave)) {
            return false;
        }

        return ((TileSlave) other).getPos().equals(pos);
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}

package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageCapabilities;
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

    private Set<String> visited = new HashSet<String>();

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    @Override
    public boolean canSendConnectivityUpdate() {
        return true;
    }

    @Override
    public void connect(World world, NetworkMaster network) {
        if (network.canRun()) {
            this.network = network;
            this.connected = true;

            this.network.addSlave(pos);

            world.notifyNeighborsOfStateChange(pos, getBlockType());

            if (canSendConnectivityUpdate()) {
                RefinedStorageUtils.updateBlock(world, pos);
            }
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
            this.network.removeSlave(pos);
            this.network = null;
        }

        world.notifyNeighborsOfStateChange(pos, getBlockType());

        if (canSendConnectivityUpdate()) {
            RefinedStorageUtils.updateBlock(world, pos);
        }
    }

    @Override
    public void onNeighborChanged(World world) {
        visited.clear();

        TileController controller = searchController(world, pos, visited);

        if (network == null) {
            if (controller != null) {
                // For backwards compatiblity
                NetworkMaster network = NetworkMasterRegistry.get(controller.getPos(), world.provider.getDimension());

                if (network != null) {
                    connect(world, network);
                }
            }
        } else {
            if (controller == null) {
                disconnect(world);
            }
        }
    }

    private TileController searchController(World world, BlockPos current, Set<String> visited) {
        String id = current.getX() + "," + current.getY() + "," + current.getZ();

        if (visited.contains(id)) {
            return null;
        }

        visited.add(id);

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
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == RefinedStorageCapabilities.NETWORK_SLAVE_CAPABILITY) {
            return (T) this;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == RefinedStorageCapabilities.NETWORK_SLAVE_CAPABILITY || super.hasCapability(capability, facing);
    }
}

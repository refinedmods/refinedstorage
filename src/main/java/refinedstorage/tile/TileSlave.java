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
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.controller.TileController;

import java.util.HashSet;
import java.util.Set;

public abstract class TileSlave extends TileBase implements INetworkSlave, ISynchronizedContainer, IRedstoneModeConfig {
    public static final String NBT_CONNECTED = "Connected";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;

    protected boolean connected;
    protected INetworkMaster network;

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
    public void update() {
        if (!worldObj.isRemote) {
            if (ticks == 0) {
                refreshConnection(worldObj);
            }

            if (isActive()) {
                updateSlave();
            }

            if (active != isActive()) {
                RefinedStorageUtils.updateBlock(worldObj, pos);

                active = isActive();
            }
        }

        super.update();
    }

    @Override
    public void connect(World world, INetworkMaster network) {
        if (network.canRun()) {
            this.network = network;
            this.connected = true;

            if (!(this instanceof TileCable)) {
                this.network.addSlave(this);
            }

            world.notifyNeighborsOfStateChange(pos, getBlockType());

            if (canSendConnectivityUpdate()) {
                RefinedStorageUtils.updateBlock(world, pos);
            }
        }
    }

    @Override
    public void disconnect(World world) {
        this.connected = false;

        if (this.network != null) {
            this.network.removeSlave(this);
            this.network = null;
        }

        world.notifyNeighborsOfStateChange(pos, getBlockType());

        if (canSendConnectivityUpdate()) {
            RefinedStorageUtils.updateBlock(world, pos);
        }
    }

    @Override
    public void refreshConnection(World world) {
        TileController controller = searchController(world, pos, new HashSet<Long>());

        if (network == null) {
            if (controller != null) {
                connect(world, controller);
            }
        } else {
            if (controller == null) {
                disconnect(world);
            }
        }
    }

    private TileController searchController(World world, BlockPos current, Set<Long> visits) {
        long pos = current.toLong();

        if (visits.contains(pos)) {
            return null;
        }

        visits.add(pos);

        TileEntity tile = world.getTileEntity(current);

        if (tile instanceof TileController) {
            return (TileController) tile;
        } else if (tile instanceof TileSlave) {
            if (visits.size() > 1 && tile instanceof TileRelay && !((TileRelay) tile).canUpdate()) {
                return null;
            }

            for (EnumFacing dir : EnumFacing.VALUES) {
                TileController controller = searchController(world, current.offset(dir), visits);

                if (controller != null) {
                    return controller;
                }
            }
        }

        return null;
    }

    @Override
    public INetworkMaster getNetwork() {
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

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TileSlave && ((TileSlave) other).getPos().equals(pos);
    }
}

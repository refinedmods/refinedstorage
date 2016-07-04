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
import refinedstorage.api.network.INetworkNode;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.*;

public abstract class TileNode extends TileBase implements INetworkNode, ISynchronizedContainer, IRedstoneModeConfig {
    private static final String NBT_CONNECTED = "Connected";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;
    private boolean update;

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
            if (update != canUpdate() && network != null) {
                update = canUpdate();

                onConnectionChange(network, update);
            }

            if (isActive()) {
                updateNode();
            }

            if (active != isActive() && canSendConnectivityUpdate()) {
                RefinedStorageUtils.updateBlock(worldObj, pos);

                active = isActive();
            }
        }

        super.update();
    }

    @Override
    public void onPlaced(World world) {
        List<INetworkNode> nodes = new ArrayList<INetworkNode>();
        Set<BlockPos> nodesPos = new HashSet<BlockPos>();

        Queue<BlockPos> positions = new ArrayDeque<BlockPos>();
        Set<BlockPos> checked = new HashSet<BlockPos>();

        nodes.add(this);
        positions.add(pos);

        INetworkMaster master = null;

        BlockPos currentPos;

        while ((currentPos = positions.poll()) != null) {
            TileEntity tile = world.getTileEntity(currentPos);

            if (tile instanceof INetworkMaster) {
                master = (INetworkMaster) tile;
                continue;
            }

            if (tile == null || !tile.hasCapability(RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY, null)) {
                continue;
            }

            INetworkNode node = tile.getCapability(RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY, null);

            nodes.add(node);
            nodesPos.add(node.getPosition());

            for (EnumFacing sideOnCurrent : EnumFacing.VALUES) {
                BlockPos sidePos = currentPos.offset(sideOnCurrent);

                if (checked.add(sidePos)) {
                    positions.add(sidePos);
                }
            }
        }

        if (master != null) {
            for (INetworkNode newNode : nodes) {
                boolean isNew = false;

                for (INetworkNode oldNode : master.getNodes()) {
                    if (oldNode.getPosition().equals(newNode.getPosition())) {
                        isNew = true;
                        break;
                    }
                }

                if (!isNew) {
                    newNode.onConnected(master);
                }
            }

            master.setNodes(nodes);
        }
    }

    @Override
    public void onBreak(World world) {
        List<INetworkNode> nodes = new ArrayList<INetworkNode>();
        Set<BlockPos> nodesPos = new HashSet<BlockPos>();

        Queue<BlockPos> positions = new ArrayDeque<BlockPos>();
        Set<BlockPos> checked = new HashSet<BlockPos>();

        checked.add(pos);

        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos sidePos = pos.offset(side);

            if (!checked.add(sidePos)) {
                continue;
            }

            positions.add(sidePos);

            BlockPos currentPos;

            while ((currentPos = positions.poll()) != null) {
                TileEntity tile = world.getTileEntity(currentPos);

                if (tile == null || !tile.hasCapability(RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY, null)) {
                    continue;
                }

                INetworkNode node = tile.getCapability(RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY, null);

                nodes.add(node);
                nodesPos.add(currentPos);

                for (EnumFacing sideOfCurrent : EnumFacing.VALUES) {
                    BlockPos sideOfCurrentPos = currentPos.offset(sideOfCurrent);

                    if (checked.add(sideOfCurrentPos)) {
                        positions.add(sideOfCurrentPos);
                    }
                }
            }
        }

        List<INetworkNode> oldNodes = network.getNodes();

        network.setNodes(nodes);

        for (INetworkNode oldNode : oldNodes) {
            if (!nodesPos.contains(oldNode.getPosition())) {
                oldNode.onDisconnected();
            }
        }
    }

    @Override
    public void onConnected(INetworkMaster network) {
        onConnectionChange(network, true);

        this.connected = true;
        this.network = network;

        if (canSendConnectivityUpdate()) {
            RefinedStorageUtils.updateBlock(worldObj, pos);
        }
    }

    @Override
    public void onDisconnected() {
        onConnectionChange(network, false);

        this.connected = false;
        this.network = null;

        if (canSendConnectivityUpdate()) {
            RefinedStorageUtils.updateBlock(worldObj, pos);
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        // NO OP
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
        this.redstoneMode = mode;

        markDirty();
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
        if (capability == RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY) {
            return (T) this;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == RefinedStorageCapabilities.NETWORK_NODE_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TileNode && ((TileNode) other).getPosition().equals(getPosition());
    }
}

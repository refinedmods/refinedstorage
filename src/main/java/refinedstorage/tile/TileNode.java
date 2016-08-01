package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.apiimpl.network.NetworkUtils;
import refinedstorage.block.BlockNode;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

public abstract class TileNode extends TileBase implements INetworkNode, ISynchronizedContainer, IRedstoneModeConfig {
    private static final String NBT_CONNECTED = "Connected";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;
    private boolean update;

    protected boolean rebuildOnUpdateChange;
    protected boolean connected;
    protected INetworkMaster network;

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    private boolean canSendConnectivityUpdate() {
        Block block = getBlockType();

        return block instanceof BlockNode ? ((BlockNode) block).hasConnectivityState() : false;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (update != canUpdate() && network != null) {
                update = canUpdate();

                onConnectionChange(network, update);

                if (rebuildOnUpdateChange) {
                    NetworkUtils.rebuildGraph(network);
                }
            }

            if (active != isActive() && canSendConnectivityUpdate()) {
                updateBlock();

                active = isActive();
            }

            if (isActive()) {
                updateNode();
            }
        }

        super.update();
    }

    @Override
    public void onConnected(INetworkMaster network) {
        this.connected = true;
        this.network = network;

        onConnectionChange(network, true);
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        onConnectionChange(network, false);

        this.connected = false;
        this.network = null;
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        // NO OP
    }

    @Override
    public boolean canConduct() {
        return true;
    }

    @Override
    public INetworkMaster getNetwork() {
        return network;
    }

    @Override
    public World getNodeWorld() {
        return worldObj;
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
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

        if (canSendConnectivityUpdate()) {
            tag.setBoolean(NBT_CONNECTED, isActive());
        }

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        if (canSendConnectivityUpdate()) {
            connected = tag.getBoolean(NBT_CONNECTED);
        }

        super.readUpdate(tag);
    }
}

package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageCapabilities;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

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

            if (active != isActive() && canSendConnectivityUpdate()) {
                RefinedStorageUtils.updateBlock(worldObj, pos);

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
        onConnectionChange(network, true);

        this.connected = true;
        this.network = network;
    }

    @Override
    public void onDisconnected() {
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

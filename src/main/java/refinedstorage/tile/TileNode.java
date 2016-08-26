package refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.tile.config.IRedstoneConfigurable;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.data.TileDataParameter;

public abstract class TileNode extends TileBase implements INetworkNode, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_CONNECTED = "Connected";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;
    private boolean update;
    private boolean connected;

    protected boolean rebuildOnUpdateChange;

    public TileNode() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (update != canUpdate() && getNetwork() != null) {
                update = canUpdate();

                onConnectionChange(getNetwork(), update);

                if (rebuildOnUpdateChange) {
                    NetworkUtils.rebuildGraph(getNetwork());
                }
            }

            if (active != isActive() && hasConnectivityState()) {
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
        onConnectionChange(network, true);
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        onConnectionChange(network, false);
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        // NO OP
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return true;
    }

    @Override
    public INetworkMaster getNetwork() {
        return !worldObj.isRemote ? RefinedStorageAPI.getNetwork(this) : null;
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
        return worldObj.isRemote ? connected : (getNetwork() != null);
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.ordinal());

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        if (hasConnectivityState()) {
            tag.setBoolean(NBT_CONNECTED, isActive());
        }

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        if (hasConnectivityState()) {
            connected = tag.getBoolean(NBT_CONNECTED);
        }

        super.readUpdate(tag);
    }

    public boolean hasConnectivityState() {
        return false;
    }
}

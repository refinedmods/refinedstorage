package refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.container.ContainerStorage;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.network.MessagePriorityUpdate;
import refinedstorage.tile.IConnectionHandler;
import refinedstorage.tile.IStorageGui;
import refinedstorage.tile.TileNode;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.ModeConstants;

import java.util.ArrayList;
import java.util.List;

public class TileExternalStorage extends TileNode implements IStorageProvider, IStorageGui, ICompareConfig, IModeConfig, IConnectionHandler {
    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private BasicItemHandler filters = new BasicItemHandler(9, this);

    private int priority = 0;
    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    private int stored;
    private int capacity;

    private List<ExternalStorage> storages = new ArrayList<ExternalStorage>();
    private int lastDrawerCount;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.externalStorageUsage + (storages.size() * RefinedStorage.INSTANCE.externalStoragePerStorageUsage);
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void update() {
        super.update();

        if (getFacingTile() instanceof IDrawerGroup && lastDrawerCount != ((IDrawerGroup) getFacingTile()).getDrawerCount()) {
            lastDrawerCount = ((IDrawerGroup) getFacingTile()).getDrawerCount();

            updateStorage();
        }
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(priority);
        buf.writeInt(getStored());
        buf.writeInt(getCapacity());
        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        priority = buf.readInt();
        stored = buf.readInt();
        capacity = buf.readInt();
        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerStorage.class;
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(filters, 0, nbt);

        if (nbt.hasKey(NBT_PRIORITY)) {
            priority = nbt.getInteger(NBT_PRIORITY);
        }

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(filters, 0, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    // Called when the neighbor block changes or when a drawer is added or removed to a drawer group
    public void updateStorage() {
        storages.clear();

        TileEntity facing = getFacingTile();

        if (facing instanceof IDrawerGroup) {
            IDrawerGroup group = (IDrawerGroup) facing;

            for (int i = 0; i < group.getDrawerCount(); ++i) {
                if (group.isDrawerEnabled(i)) {
                    storages.add(new DrawerStorage(this, group.getDrawer(i)));
                }
            }
        } else if (facing instanceof IDrawer) {
            storages.add(new DrawerStorage(this, (IDrawer) facing));
        } else if (facing instanceof IDeepStorageUnit) {
            storages.add(new DeepStorageUnitStorage(this, (IDeepStorageUnit) facing));
        } else {
            IItemHandler handler = RefinedStorageUtils.getItemHandler(facing, getDirection().getOpposite());

            if (handler != null) {
                storages.add(new ItemHandlerStorage(this, handler));
            }
        }
    }

    @Override
    public void addStorages(List<IStorage> storages) {
        storages.addAll(this.storages);
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:external_storage";
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeConfig() {
        return this;
    }

    @Override
    public ICompareConfig getCompareConfig() {
        return this;
    }

    @Override
    public IModeConfig getModeConfig() {
        return this;
    }

    @Override
    public int getStored() {
        if (!worldObj.isRemote) {
            int stored = 0;

            for (ExternalStorage storage : storages) {
                stored += storage.getStored();
            }

            return stored;
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        if (!worldObj.isRemote) {
            int capacity = 0;

            for (ExternalStorage storage : storages) {
                capacity += storage.getCapacity();
            }

            return capacity;
        }

        return capacity;
    }

    @Override
    public void onPriorityChanged(int priority) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessagePriorityUpdate(pos, priority));
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
    }

    @Override
    public void onConnected(INetworkMaster network) {
        network.getStorage().rebuild();
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        network.getStorage().rebuild();
    }
}

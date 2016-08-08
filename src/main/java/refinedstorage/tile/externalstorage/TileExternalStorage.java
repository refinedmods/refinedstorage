package refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import mcmultipart.microblock.IMicroblock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.tile.IStorageGui;
import refinedstorage.tile.TileMultipartNode;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IPrioritizable;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

import java.util.ArrayList;
import java.util.List;

public class TileExternalStorage extends TileMultipartNode implements IStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();

    public static final TileDataParameter<Integer> STORED = TileDataManager.createParameter(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileExternalStorage>() {
        @Override
        public Integer getValue(TileExternalStorage tile) {
            return tile.getStored();
        }
    });

    public static final TileDataParameter<Integer> CAPACITY = TileDataManager.createParameter(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileExternalStorage>() {
        @Override
        public Integer getValue(TileExternalStorage tile) {
            return tile.getCapacity();
        }
    });

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);

    private int priority = 0;
    private int compare = 0;
    private int mode = IFilterable.WHITELIST;

    private List<ExternalStorage> storages = new ArrayList<>();
    private int lastDrawerCount;

    public TileExternalStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.externalStorageUsage + (storages.size() * RefinedStorage.INSTANCE.externalStoragePerStorageUsage);
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        updateStorage(network);

        network.getStorage().rebuild();
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && network != null) {
            if (ticks % (20 * 4) == 0) {
                boolean shouldRebuild = false;

                for (ExternalStorage storage : storages) {
                    if (storage.updateCache()) {
                        shouldRebuild = true;
                    }
                }

                if (shouldRebuild) {
                    network.getStorage().rebuild();
                }
            }

            if (getFacingTile() instanceof IDrawerGroup && lastDrawerCount != ((IDrawerGroup) getFacingTile()).getDrawerCount()) {
                lastDrawerCount = ((IDrawerGroup) getFacingTile()).getDrawerCount();

                updateStorage(network);
            }
        }

        super.update();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(filters, 0, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(filters, 0, tag);

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

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    public void updateStorage(INetworkMaster network) {
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
            IItemHandler handler = getItemHandler(facing, getDirection().getOpposite());

            if (handler != null) {
                storages.add(new ItemHandlerStorage(this, handler));
            }
        }

        network.getStorage().rebuild();
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
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return PRIORITY;
    }

    @Override
    public int getStored() {
        int stored = 0;

        for (ExternalStorage storage : storages) {
            stored += storage.getStored();
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (ExternalStorage storage : storages) {
            capacity += storage.getCapacity();
        }

        return capacity;
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
    }
}

package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.apiimpl.storage.NBTStorage;
import refinedstorage.block.BlockStorage;
import refinedstorage.block.EnumStorageType;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.tile.config.*;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public class TileStorage extends TileNode implements IStorageProvider, IStorageGui, ICompareConfig, IModeConfig, IPrioritizable {
    public static final TileDataParameter PRIORITY = IPrioritizable.createConfigParameter();
    public static final TileDataParameter COMPARE = ICompareConfig.createConfigParameter();
    public static final TileDataParameter MODE = IModeConfig.createConfigParameter();
    public static final TileDataParameter STORED = TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, TileStorage>() {
        @Override
        public Integer getValue(TileStorage tile) {
            return NBTStorage.getStoredFromNBT(tile.storageTag);
        }
    });

    class Storage extends NBTStorage {
        public Storage() {
            super(TileStorage.this.getStorageTag(), TileStorage.this.getCapacity(), TileStorage.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!ModeFilter.respectsMode(filters, TileStorage.this, compare, stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insertItem(stack, size, simulate);
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);

    private NBTTagCompound storageTag = NBTStorage.createNBT();

    private Storage storage;

    private EnumStorageType type;

    private int priority = 0;
    private int compare = 0;
    private int mode = IModeConfig.WHITELIST;

    public TileStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.storageUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new Storage();

            if (network != null) {
                network.getStorage().rebuild();
            }
        }
    }

    public void onBreak() {
        if (storage != null) {
            storage.writeToNBT();
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getStorage().rebuild();
    }

    @Override
    public void addStorages(List<IStorage> storages) {
        if (storage != null) {
            storages.add(storage);
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(filters, 0, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_STORAGE)) {
            storageTag = tag.getCompoundTag(NBT_STORAGE);
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

        if (storage != null) {
            storage.writeToNBT();
        }

        tag.setTag(NBT_STORAGE, storageTag);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
    }

    public EnumStorageType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.STORAGE) {
            this.type = ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
        }

        return type == null ? EnumStorageType.TYPE_1K : type;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        if (worldObj.isRemote) {
            TileDataManager.setParameter(COMPARE, compare);
        } else {
            this.compare = compare;

            markDirty();
        }
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        if (worldObj.isRemote) {
            TileDataManager.setParameter(MODE, mode);
        } else {
            this.mode = mode;

            markDirty();
        }
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:storage." + getType().getId() + ".name";
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
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

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public NBTStorage getStorage() {
        return storage;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void onPriorityChanged(int priority) {
        TileDataManager.setParameter(PRIORITY, priority);
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    @Override
    public int getStored() {
        return (int) STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity();
    }
}

package refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IFluidStorageProvider;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.block.BlockFluidStorage;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IPrioritizable;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public class TileFluidStorage extends TileNode implements IFluidStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileFluidStorage>() {
        @Override
        public Integer getValue(TileFluidStorage tile) {
            return FluidStorageNBT.getStoredFromNBT(tile.storageTag);
        }
    });

    class FluidStorage extends FluidStorageNBT {
        public FluidStorage() {
            super(TileFluidStorage.this.getStorageTag(), TileFluidStorage.this.getCapacity(), TileFluidStorage.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(filters, mode, compare, stack)) {
                return FluidUtils.copyStackWithSize(stack, size);
            }

            return super.insertFluid(stack, size, simulate);
        }
    }

    public static final String NBT_STORAGE = "Storage";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private ItemHandlerFluid filters = new ItemHandlerFluid(9, this);

    private NBTTagCompound storageTag = FluidStorageNBT.createNBT();

    private FluidStorage storage;

    private EnumFluidStorageType type;

    private int priority = 0;
    private int compare = 0;
    private int mode = IFilterable.WHITELIST;

    public TileFluidStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.fluidStorageUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void update() {
        super.update();

        if (storage == null && storageTag != null) {
            storage = new FluidStorage();

            if (getNetwork() != null) {
                getNetwork().getFluidStorage().rebuild();
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

        network.getFluidStorage().rebuild();
    }

    @Override
    public void addFluidStorages(List<IFluidStorage> storages) {
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

    public EnumFluidStorageType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.FLUID_STORAGE) {
            this.type = ((EnumFluidStorageType) worldObj.getBlockState(pos).getValue(BlockFluidStorage.TYPE));
        }

        return type == null ? EnumFluidStorageType.TYPE_64K : type;
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
    public String getGuiTitle() {
        return "block.refinedstorage:fluid_storage." + getType().getId() + ".name";
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return null;
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

    public NBTTagCompound getStorageTag() {
        return storageTag;
    }

    public void setStorageTag(NBTTagCompound storageTag) {
        this.storageTag = storageTag;
    }

    public FluidStorageNBT getStorage() {
        return storage;
    }

    public ItemHandlerFluid getFilters() {
        return filters;
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

    @Override
    public int getStored() {
        return STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity();
    }
}


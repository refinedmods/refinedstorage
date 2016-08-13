package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IFluidStorageProvider;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluidFilter;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IPrioritizable;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public class TileFluidDiskDrive extends TileNode implements IFluidStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();

    public class FluidStorage extends FluidStorageNBT {
        public FluidStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), TileFluidDiskDrive.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(filters, mode, getCompare(), stack)) {
                return FluidUtils.copyStackWithSize(stack, size);
            }

            return super.insertFluid(stack, size, simulate);
        }
    }

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_STORED = "Stored";

    private ItemHandlerBasic disks = new ItemHandlerBasic(8, this, new ItemValidatorBasic(RefinedStorageItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    }) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                ItemStack disk = getStackInSlot(slot);

                if (disk == null) {
                    storages[slot] = null;
                } else {
                    storages[slot] = new FluidStorage(disk);
                }

                if (network != null) {
                    network.getFluidStorage().rebuild();
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (storages[slot] != null) {
                storages[slot].writeToNBT();
            }

            return super.extractItem(slot, amount, simulate);
        }
    };

    private ItemHandlerFluidFilter filters = new ItemHandlerFluidFilter(this);

    private FluidStorage storages[] = new FluidStorage[8];

    private int priority = 0;
    private int compare = 0;
    private int mode = IFilterable.WHITELIST;
    private int stored = 0;

    public TileFluidDiskDrive() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (stored != getStoredForDisplayServer()) {
                stored = getStoredForDisplayServer();

                updateBlock();
            }
        }

        super.update();
    }

    @Override
    public int getEnergyUsage() {
        int usage = RefinedStorage.INSTANCE.fluidDiskDriveUsage;

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (disks.getStackInSlot(i) != null) {
                usage += RefinedStorage.INSTANCE.fluidDiskDrivePerDiskUsage;
            }
        }

        return usage;
    }

    @Override
    public void updateNode() {
    }

    public void onBreak() {
        for (FluidStorage storage : this.storages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getFluidStorage().rebuild();
    }

    @Override
    public void addFluidStorages(List<IFluidStorage> storages) {
        for (IFluidStorage storage : this.storages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(disks, 0, tag);
        readItems(filters, 1, tag);

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

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (storages[i] != null) {
                storages[i].writeToNBT();
            }
        }

        writeItems(disks, 0, tag);
        writeItems(filters, 1, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_STORED, stored);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        stored = tag.getInteger(NBT_STORED);

        super.readUpdate(tag);
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

    public int getStoredForDisplayServer() {
        float stored = 0;
        float storedMax = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack disk = disks.getStackInSlot(i);

            if (disk != null) {
                int capacity = EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

                if (capacity == -1) {
                    return 0;
                }

                stored += FluidStorageNBT.getStoredFromNBT(disk.getTagCompound());
                storedMax += EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();
            }
        }

        if (storedMax == 0) {
            return 0;
        }

        return (int) Math.floor((stored / storedMax) * 7f);
    }

    public int getStoredForDisplay() {
        return stored;
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:fluid_disk_drive.name";
    }

    @Override
    public IItemHandler getFilters() {
        return filters;
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
    public boolean hasComparisonFor(int compare) {
        return compare == CompareUtils.COMPARE_NBT;
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
        int stored = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack stack = disks.getStackInSlot(i);

            if (stack != null) {
                stored += FluidStorageNBT.getStoredFromNBT(stack.getTagCompound());
            }
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack stack = disks.getStackInSlot(i);

            if (stack != null) {
                int diskCapacity = EnumFluidStorageType.getById(stack.getItemDamage()).getCapacity();

                if (diskCapacity == -1) {
                    return -1;
                }

                capacity += diskCapacity;
            }
        }

        return capacity;
    }

    public IItemHandler getDisks() {
        return disks;
    }

    @Override
    public IItemHandler getDrops() {
        return disks;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) disks;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

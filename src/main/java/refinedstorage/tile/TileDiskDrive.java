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
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RS;
import refinedstorage.RSItems;
import refinedstorage.RSUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IFluidStorageProvider;
import refinedstorage.api.storage.item.IItemStorage;
import refinedstorage.api.storage.item.IItemStorageProvider;
import refinedstorage.api.util.IComparer;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.block.EnumItemStorageType;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.tile.config.*;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public class TileDiskDrive extends TileNode implements IItemStorageProvider, IFluidStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable, IType, IExcessVoidable, IAccessType {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> VOID_EXCESS = IExcessVoidable.createParameter();
    public static final TileDataParameter<Integer> ACCESS_TYPE = IAccessType.createParameter();

    public class ItemStorage extends ItemStorageNBT {
        public ItemStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumItemStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskDrive.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            ItemStack result = super.insertItem(stack, size, simulate);

            if (voidExcess && result != null) {
                // Simulate should not matter as the items are voided anyway
                result.stackSize = -result.stackSize;
            }

            return result;
        }

        @Override
        public int getAccessType() {
            return accessType;
        }
    }

    public class FluidStorage extends FluidStorageNBT {
        public FluidStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskDrive.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return RSUtils.copyStackWithSize(stack, size);
            }

            FluidStack result = super.insertFluid(stack, size, simulate);

            if (voidExcess && result != null) {
                // Simulate should not matter as the fluids are voided anyway
                result.amount = -result.amount;
            }

            return result;
        }

        @Override
        public int getAccessType() {
            return accessType;
        }
    }

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_VOID_EXCESS = "VoidExcess";
    private static final String NBT_ACCESS_TYPE = "AccessType";

    private ItemHandlerBasic disks = new ItemHandlerBasic(8, this, IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                RSUtils.constructFromDrive(getStackInSlot(slot), slot, itemStorages, fluidStorages, ItemStorage::new, FluidStorage::new);

                if (network != null) {
                    network.getItemStorage().rebuild();
                    network.getFluidStorage().rebuild();
                }

                if (worldObj != null) {
                    updateBlock();
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (itemStorages[slot] != null) {
                itemStorages[slot].writeToNBT();
            }

            if (fluidStorages[slot] != null) {
                fluidStorages[slot].writeToNBT();
            }

            return super.extractItem(slot, amount, simulate);
        }
    };

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemStorage itemStorages[] = new ItemStorage[8];
    private FluidStorage fluidStorages[] = new FluidStorage[8];

    private int accessType = IAccessType.READ_WRITE;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private boolean voidExcess = false;

    public TileDiskDrive() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(VOID_EXCESS);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.INSTANCE.config.diskDriveUsage;

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (disks.getStackInSlot(i) != null) {
                usage += RS.INSTANCE.config.diskDrivePerDiskUsage;
            }
        }

        return usage;
    }

    @Override
    public void updateNode() {
    }

    public void onBreak() {
        for (ItemStorage storage : this.itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (FluidStorage storage : this.fluidStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getItemStorage().rebuild();
        network.getFluidStorage().rebuild();
    }

    @Override
    public void addItemStorages(List<IItemStorage> storages) {
        for (IItemStorage storage : this.itemStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void addFluidStorages(List<IFluidStorage> storages) {
        for (IFluidStorage storage : this.fluidStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(disks, 0, tag);
        RSUtils.readItems(itemFilters, 1, tag);
        RSUtils.readItems(fluidFilters, 2, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        if (tag.hasKey(NBT_VOID_EXCESS)) {
            voidExcess = tag.getBoolean(NBT_VOID_EXCESS);
        }

        if (tag.hasKey(NBT_ACCESS_TYPE)) {
            accessType = tag.getInteger(NBT_ACCESS_TYPE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (itemStorages[i] != null) {
                itemStorages[i].writeToNBT();
            }

            if (fluidStorages[i] != null) {
                fluidStorages[i].writeToNBT();
            }
        }

        RSUtils.writeItems(disks, 0, tag);
        RSUtils.writeItems(itemFilters, 1, tag);
        RSUtils.writeItems(fluidFilters, 2, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);
        tag.setBoolean(NBT_VOID_EXCESS, voidExcess);
        tag.setInteger(NBT_ACCESS_TYPE, accessType);

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
    public String getGuiTitle() {
        return "block.refinedstorage:disk_drive.name";
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return TYPE;
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
    public TileDataParameter<Boolean> getVoidExcessParameter() {
        return VOID_EXCESS;
    }

    @Override
    public TileDataParameter<Integer> getAccessTypeParameter() {
        return ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return "items_fluids";
    }

    @Override
    public int getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(int value) {
        accessType = value;

        network.getFluidStorage().rebuild();
        network.getItemStorage().rebuild();

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

    @Override
    public int getStored() {
        int stored = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack disk = disks.getStackInSlot(i);

            if (disk != null) {
                stored += disk.getItem() == RSItems.STORAGE_DISK ? ItemStorageNBT.getStoredFromNBT(disk.getTagCompound()) : FluidStorageNBT.getStoredFromNBT(disk.getTagCompound());
            }
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack disk = disks.getStackInSlot(i);

            if (disk != null) {
                int diskCapacity = disk.getItem() == RSItems.STORAGE_DISK ? EnumItemStorageType.getById(disk.getItemDamage()).getCapacity() : EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

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
    public boolean getVoidExcess() {
        return voidExcess;
    }

    @Override
    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;

        markDirty();
    }

    @Override
    public int getType() {
        return worldObj.isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
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

package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.block.EnumItemStorageType;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.data.TileDataParameter;

public class TileDiskManipulator extends TileNode implements IComparable, IFilterable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";

    private int compare = 0;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;

    public TileDiskManipulator() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
    }

    private ItemHandlerBasic disks = new ItemHandlerBasic(12, this, new ItemValidatorBasic(RefinedStorageItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    }, new ItemValidatorBasic(RefinedStorageItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    });

    public class ItemStorage extends ItemStorageNBT {
        public ItemStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumItemStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskManipulator.this);
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insertItem(stack, size, simulate);
        }
    }

    public class FluidStorage extends FluidStorageNBT {
        public FluidStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskManipulator.this);
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return FluidUtils.copyStackWithSize(stack, size);
            }

            return super.insertFluid(stack, size, simulate);
        }
    }

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public void updateNode() {
        int i = 0;
        ItemStack disk = disks.getStackInSlot(i);
        while (disk == null && i < 6) i++;
        if (disk == null) return;

        if (disk.getItem() == RefinedStorageItems.STORAGE_DISK) {
            ItemStorage storage = new ItemStorage(disk);
        } else if (disk.getItem() == RefinedStorageItems.FLUID_STORAGE_DISK) {
            FluidStorage storage = new FluidStorage(disk);
        }

    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getMode() {
        return this.mode;
    }

    public IItemHandler getDisks() {
        return disks;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(disks, 0, tag);
        readItems(itemFilters, 1, tag);
        readItems(fluidFilters, 2, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(disks, 0, tag);
        writeItems(itemFilters, 1, tag);
        writeItems(fluidFilters, 2, tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        return tag;
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

package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.apiimpl.storage.NBTStorage;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.block.EnumItemStorageType;
import refinedstorage.inventory.IItemValidator;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public class TileDiskManipulator extends TileNode implements IComparable, IFilterable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public static final int INSERT = 0, EXTRACT = 1;
    public static final TileDataParameter<Integer> IO_MODE = new TileDataParameter<>(DataSerializers.VARINT, INSERT, new ITileDataProducer<Integer, TileDiskManipulator>() {
        @Override
        public Integer getValue(TileDiskManipulator tile) {
            return tile.ioMode;
        }
    }, new ITileDataConsumer<Integer, TileDiskManipulator>() {
        @Override
        public void setValue(TileDiskManipulator tile, Integer value) {
            tile.ioMode = value;
            tile.markDirty();
        }
    });

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";

    private int compare = 0;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private int ioMode = INSERT;

    private ItemStorage[] itemStorages;
    private FluidStorage[] fluidStorages;

    public TileDiskManipulator() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

        itemStorages = new ItemStorage[6];
        fluidStorages = new FluidStorage[6];
    }

    private ItemHandlerBasic disks = new ItemHandlerBasic(12, this, IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && slot < 6) {
                NBTStorage.constructFromDrive(getStackInSlot(slot), slot, itemStorages, fluidStorages, s -> new ItemStorage(s), s -> new FluidStorage(s));
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot < 6) {
                if (itemStorages[slot] != null) {
                    itemStorages[slot].writeToNBT();
                }

                if (fluidStorages[slot] != null) {
                    fluidStorages[slot].writeToNBT();
                }
            }
            return super.extractItem(slot, amount, simulate);
        }
    };

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

        @Override
        public ItemStack extractItem(ItemStack stack, int size, int flags) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return null;
            }

            return super.extractItem(stack, size, flags);
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

        @Override
        public FluidStack extractFluid(FluidStack stack, int size, int flags) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return null;
            }

            return super.extractFluid(stack, size, flags);
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
        int slot = 0;
        if (type == IType.ITEMS) {
            while (slot < itemStorages.length && itemStorages[slot] == null) slot++;
            if (slot == itemStorages.length) return;
            ItemStorage storage = itemStorages[slot];
            if (ioMode == INSERT) {
                insertIntoNetwork(storage, slot);
            } else if (ioMode == EXTRACT) {
                extractFromNetwork(storage, slot);
            }

        } else if (type == IType.FLUIDS) {
            while (slot < fluidStorages.length && fluidStorages[slot] == null) slot++;
            if (slot == fluidStorages.length) return;
            FluidStorage storage = fluidStorages[slot];
            if (ioMode == INSERT) {
                insertIntoNetwork(storage, slot);
            } else if (ioMode == EXTRACT) {
                extractFromNetwork(storage, slot);
            }
        }
    }

    private void insertIntoNetwork(ItemStorage storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }
        ItemStack extracted = null;
        int ii = 0;
        do {
            ItemStack stack = null;
            while (storage.getItems().size() > ii && stack == null) {
                stack = storage.getItems().get(ii++);
            }
            if (stack != null) {
                extracted = storage.extractItem(stack, 1, compare);
            }
        } while (storage.getItems().size() > ii && extracted == null);
        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }
        ItemStack leftOver = network.insertItem(extracted, extracted.stackSize, false);
        if (leftOver != null) {
            storage.insertItem(leftOver, leftOver.stackSize, false);
        }
    }

    private void extractFromNetwork(ItemStorage storage, int slot) {

    }

    private void insertIntoNetwork(FluidStorage storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }
        FluidStack extracted = null;
        int ii = 0;
        do {
            FluidStack stack = storage.getStacks().get(ii);
            while (stack == null && storage.getStacks().size() > ii) {
                ii++;
            }
            if (stack != null) {
                extracted = storage.extractFluid(stack, 1, compare);
            }
        } while (extracted == null && storage.getStacks().size() > ii);
        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }
        FluidStack leftOver = network.insertFluid(extracted, extracted.amount, false);
        if (leftOver != null) {
            storage.insertFluid(leftOver, leftOver.amount, false);
        }
    }

    private void extractFromNetwork(FluidStorage storage, int slot) {
    }

    private void moveDriveToOutput(int slot) {
        ItemStack disk = disks.getStackInSlot(slot);
        if (disk != null) {
            int i = 6;
            while (disks.getStackInSlot(i) != null && i < 12) {
                i++;
            }
            if (i == 12) {
                return;
            }
            if (slot < 6) {
                if (itemStorages[slot] != null) {
                    itemStorages[slot].writeToNBT();
                    itemStorages[slot] = null;
                }
                if (fluidStorages[slot] != null) {
                    fluidStorages[slot].writeToNBT();
                    fluidStorages[slot] = null;
                }
            }
            disks.extractItem(slot, 1, false);
            disks.insertItem(i, disk, false);
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

    public void onBreak() {
        for (ItemStorage storage : itemStorages)
            if (storage != null)
                storage.writeToNBT();
        for (FluidStorage storage : fluidStorages)
            if (storage != null)
                storage.writeToNBT();
    }
}

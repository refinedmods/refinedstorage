package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import com.raoulvdberge.refinedstorage.apiimpl.storage.item.ItemStorageNBT;
import com.raoulvdberge.refinedstorage.block.EnumFluidStorageType;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.inventory.IItemValidator;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;

public class TileDiskManipulator extends TileNode implements IComparable, IFilterable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public static final int IO_MODE_INSERT = 0;
    public static final int IO_MODE_EXTRACT = 1;

    public static final TileDataParameter<Integer> IO_MODE = new TileDataParameter<>(DataSerializers.VARINT, IO_MODE_INSERT, new ITileDataProducer<Integer, TileDiskManipulator>() {
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
    private static final String NBT_IO_MODE = "IOMode";

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private int ioMode = IO_MODE_INSERT;

    private ItemStorage[] itemStorages = new ItemStorage[6];
    private FluidStorage[] fluidStorages = new FluidStorage[6];

    private Integer[] diskState = new Integer[6];

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private ItemHandlerBasic inputDisks = new ItemHandlerBasic(3, this, IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                RSUtils.constructFromDrive(getStackInSlot(slot), slot, itemStorages, fluidStorages, s -> new ItemStorage(s), s -> new FluidStorage(s));

                updateBlock();
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

    private ItemHandlerBasic outputDisks = new ItemHandlerBasic(3, this, IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                RSUtils.constructFromDrive(getStackInSlot(slot), 3 + slot, itemStorages, fluidStorages, s -> new ItemStorage(s), s -> new FluidStorage(s));

                updateBlock();
            }
        }
    };

    public class ItemStorage extends ItemStorageNBT {
        private int lastState;

        public ItemStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumItemStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskManipulator.this);

            lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
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

        @Override
        public void onStorageChanged() {
            super.onStorageChanged();

            int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

            if (lastState != currentState) {
                lastState = currentState;

                updateBlock();
            }
        }
    }

    public class FluidStorage extends FluidStorageNBT {
        private int lastState;

        public FluidStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskManipulator.this);

            lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return RSUtils.copyStackWithSize(stack, size);
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

        @Override
        public void onStorageChanged() {
            super.onStorageChanged();

            int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

            if (lastState != currentState) {
                lastState = currentState;

                updateBlock();
            }
        }
    }

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    public TileDiskManipulator() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

        TileDiskDrive.initDiskState(diskState);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.diskManipulatorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed() != 0) {
            return;
        }
        int slot = 0;
        if (type == IType.ITEMS) {
            while (slot < 3 && itemStorages[slot] == null) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            ItemStorage storage = itemStorages[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFromNetwork(storage, slot);
            }
        } else if (type == IType.FLUIDS) {
            while (slot < 3 && fluidStorages[slot] == null) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            FluidStorage storage = fluidStorages[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFromNetwork(storage, slot);
            }
        }
    }

    private void insertIntoNetwork(ItemStorage storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }

        for (int i = 0; i < storage.getStacks().size(); i++) {
            ItemStack stack = storage.getStacks().get(i);
            if (stack == null) {
                continue;
            }

            ItemStack extracted = storage.extractItem(stack, upgrades.getInteractStackSize(), compare);
            if (extracted == null) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.stackSize, false);
            if (remainder == null) {
                break;
            }

            // We need to check if the stack was inserted
            storage.insertItem(((extracted == remainder) ? remainder.copy() : remainder), remainder.stackSize, false);
        }

        if (storage.getStacks().size() == 0) {
            moveDriveToOutput(slot);
        }
    }

    private void extractFromNetwork(ItemStorage storage, int slot) {
        if (storage.getStored() == storage.getCapacity()) {
            moveDriveToOutput(slot);
            return;
        }

        ItemStack extracted = null;
        int i = 0;

        if (IFilterable.isEmpty(itemFilters)) {
            ItemStack toExtract = null;
            ArrayList<ItemStack> networkItems = new ArrayList<>(network.getItemStorageCache().getList().getStacks());

            int j = 0;

            while ((toExtract == null || toExtract.stackSize == 0) && j < networkItems.size()) {
                toExtract = networkItems.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractItem(toExtract, upgrades.getInteractStackSize(), compare);
            }
        } else {
            while (itemFilters.getSlots() > i && extracted == null) {
                ItemStack stack = null;

                while (itemFilters.getSlots() > i && stack == null) {
                    stack = itemFilters.getStackInSlot(i++);
                }

                if (stack != null) {
                    extracted = network.extractItem(stack, upgrades.getInteractStackSize(), compare);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        ItemStack remainder = storage.insertItem(extracted, extracted.stackSize, false);

        if (remainder != null) {
            network.insertItem(remainder, remainder.stackSize, false);
        }
    }

    private void insertIntoNetwork(FluidStorage storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack extracted = null;
        int i = 0;

        do {
            FluidStack stack = storage.getStacks().get(i);

            while (stack == null && storage.getStacks().size() > i) {
                i++;
            }

            if (stack != null) {
                extracted = storage.extractFluid(stack, upgrades.getInteractStackSize(), compare);
            }
        } while (extracted == null && storage.getStacks().size() > i);

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = network.insertFluid(extracted, extracted.amount, false);

        if (remainder != null) {
            storage.insertFluid(remainder, remainder.amount, false);
        }
    }

    private void extractFromNetwork(FluidStorage storage, int slot) {
        if (storage.getStored() == storage.getCapacity()) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack extracted = null;
        int i = 0;

        if (IFilterable.isEmpty(itemFilters)) {
            FluidStack toExtract = null;
            ArrayList<FluidStack> networkFluids = new ArrayList<>(network.getFluidStorageCache().getList().getStacks());

            int j = 0;

            while ((toExtract == null || toExtract.amount == 0) && j < networkFluids.size()) {
                toExtract = networkFluids.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractFluid(toExtract, upgrades.getInteractStackSize(), compare);
            }
        } else {
            while (fluidFilters.getSlots() > i && extracted == null) {
                FluidStack stack = null;

                while (fluidFilters.getSlots() > i && stack == null) {
                    stack = fluidFilters.getFluidStackInSlot(i++);
                }

                if (stack != null) {
                    extracted = network.extractFluid(stack, upgrades.getInteractStackSize(), compare);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = storage.insertFluid(extracted, extracted.amount, false);

        if (remainder != null) {
            network.insertFluid(remainder, remainder.amount, false);
        }
    }

    private void moveDriveToOutput(int slot) {
        ItemStack disk = inputDisks.getStackInSlot(slot);
        if (disk != null) {
            int i = 0;
            while (i < 3 && outputDisks.getStackInSlot(i) != null) {
                i++;
            }

            if (i == 3) {
                return;
            }

            if (slot < 3) {
                if (itemStorages[slot] != null) {
                    itemStorages[slot].writeToNBT();
                    itemStorages[slot] = null;
                }

                if (fluidStorages[slot] != null) {
                    fluidStorages[slot].writeToNBT();
                    fluidStorages[slot] = null;
                }
            }
            inputDisks.extractItem(slot, 1, false);
            outputDisks.insertItem(i, disk, false);
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

    public IItemHandler getInputDisks() {
        return inputDisks;
    }

    public IItemHandler getOutputDisks() {
        return outputDisks;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(upgrades, 3, tag);
        RSUtils.readItems(inputDisks, 4, tag);
        RSUtils.readItems(outputDisks, 5, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        onBreak();

        RSUtils.writeItems(upgrades, 3, tag);
        RSUtils.writeItems(inputDisks, 4, tag);
        RSUtils.writeItems(outputDisks, 5, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        RSUtils.writeItems(itemFilters, 1, tag);
        RSUtils.writeItems(fluidFilters, 2, tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);
        tag.setInteger(NBT_IO_MODE, ioMode);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        RSUtils.readItems(itemFilters, 1, tag);
        RSUtils.readItems(fluidFilters, 2, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        if (tag.hasKey(NBT_IO_MODE)) {
            ioMode = tag.getInteger(NBT_IO_MODE);
        }
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        TileDiskDrive.writeDiskState(tag, 6, connected, itemStorages, fluidStorages);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        TileDiskDrive.readDiskState(tag, diskState);
    }

    public Integer[] getDiskState() {
        return diskState;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(inputDisks, outputDisks, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == EnumFacing.DOWN ? (T) outputDisks : (T) inputDisks;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    public void onBreak() {
        for (ItemStorage storage : itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (FluidStorage storage : fluidStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
    }

    @Override
    public void invalidate() {
        onBreak();

        super.invalidate();
    }
}

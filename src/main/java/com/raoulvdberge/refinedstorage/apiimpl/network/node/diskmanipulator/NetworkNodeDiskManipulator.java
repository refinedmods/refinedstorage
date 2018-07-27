package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerProxy;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeDiskManipulator extends NetworkNode implements IComparable, IFilterable, IType, IStorageDiskContainerContext {
    public static final String ID = "disk_manipulator";

    public static final int IO_MODE_INSERT = 0;
    public static final int IO_MODE_EXTRACT = 1;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_IO_MODE = "IOMode";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;
    private int type = IType.ITEMS;
    private int ioMode = IO_MODE_INSERT;

    private IStorageDisk<ItemStack>[] itemDisks = new IStorageDisk[6];
    private IStorageDisk<FluidStack>[] fluidDisks = new IStorageDisk[6];

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK) {
        @Override
        public int getItemInteractCount() {
            int count = super.getItemInteractCount();

            if (type == IType.FLUIDS) {
                count *= Fluid.BUCKET_VOLUME;
            }

            return count;
        }
    };

    private ItemHandlerBase inputDisks = new ItemHandlerBase(3, new ListenerNetworkNode(this), NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    world,
                    getStackInSlot(slot),
                    slot,
                    itemDisks,
                    fluidDisks,
                    s -> new StorageDiskItemManipulatorWrapper(NetworkNodeDiskManipulator.this, s),
                    s -> new StorageDiskFluidManipulatorWrapper(NetworkNodeDiskManipulator.this, s)
                );

                WorldUtils.updateBlock(world, pos);
            }
        }
    };

    private ItemHandlerBase outputDisks = new ItemHandlerBase(3, new ListenerNetworkNode(this), NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    world,
                    getStackInSlot(slot),
                    3 + slot,
                    itemDisks,
                    fluidDisks,
                    s -> new StorageDiskItemManipulatorWrapper(NetworkNodeDiskManipulator.this, s),
                    s -> new StorageDiskFluidManipulatorWrapper(NetworkNodeDiskManipulator.this, s)
                );

                WorldUtils.updateBlock(world, pos);
            }
        }
    };

    private ItemHandlerProxy disks = new ItemHandlerProxy(inputDisks, outputDisks);

    public NetworkNodeDiskManipulator(World world, BlockPos pos) {
        super(world, pos);
    }

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.diskManipulatorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (network == null || !canUpdate() || ticks % upgrades.getSpeed() != 0) {
            return;
        }

        int slot = 0;
        if (type == IType.ITEMS) {
            while (slot < 3 && (itemDisks[slot] == null || isItemDiskDone(itemDisks[slot], slot))) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            IStorageDisk<ItemStack> storage = itemDisks[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertItemIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractItemFromNetwork(storage, slot);
            }
        } else if (type == IType.FLUIDS) {
            while (slot < 3 && (fluidDisks[slot] == null || isFluidDiskDone(fluidDisks[slot], slot))) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            IStorageDisk<FluidStack> storage = fluidDisks[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertFluidIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFluidFromNetwork(storage, slot);
            }
        }
    }

    private void insertItemIntoNetwork(IStorageDisk<ItemStack> storage, int slot) {
        List<ItemStack> stacks = new ArrayList<>(storage.getStacks());
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get(i);

            ItemStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, Action.PERFORM);
            if (extracted == null) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), Action.PERFORM);
            if (remainder == null) {
                break;
            }

            // We need to check if the stack was inserted
            storage.insert(((extracted == remainder) ? remainder.copy() : remainder), remainder.getCount(), Action.PERFORM);
        }
    }

    // Iterate through disk stacks, if none can be inserted, return that it is done processing and can be output.
    private boolean isItemDiskDone(IStorageDisk<ItemStack> storage, int slot) {
        if (ioMode == IO_MODE_INSERT && storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        // In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT)
            if (storage.getStored() == storage.getCapacity()) {
                moveDriveToOutput(slot);
                return true;
            } else {
                return false;
            }

        List<ItemStack> stacks = new ArrayList<>(storage.getStacks());
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get(i);

            ItemStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, Action.SIMULATE);
            if (extracted == null) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), Action.SIMULATE);
            if (remainder == null) { //An item could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }
        return true;
    }

    private void extractItemFromNetwork(IStorageDisk<ItemStack> storage, int slot) {
        ItemStack extracted = null;
        int i = 0;

        if (itemFilters.isEmpty()) {
            ItemStack toExtract = null;
            ArrayList<ItemStack> networkItems = new ArrayList<>(network.getItemStorageCache().getList().getStacks());

            int j = 0;

            while ((toExtract == null || toExtract.isEmpty()) && j < networkItems.size()) {
                toExtract = networkItems.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractItem(toExtract, upgrades.getItemInteractCount(), compare, Action.PERFORM);
            }
        } else {
            while (itemFilters.getSlots() > i && extracted == null) {
                ItemStack filterStack = ItemStack.EMPTY;

                while (itemFilters.getSlots() > i && filterStack.isEmpty()) {
                    filterStack = itemFilters.getStackInSlot(i++);
                }

                if (!filterStack.isEmpty()) {
                    extracted = network.extractItem(filterStack, upgrades.getItemInteractCount(), compare, Action.PERFORM);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        ItemStack remainder = storage.insert(extracted, extracted.getCount(), Action.PERFORM);

        if (remainder != null) {
            network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
        }
    }

    private void insertFluidIntoNetwork(IStorageDisk<FluidStack> storage, int slot) {
        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());

        FluidStack extracted = null;
        int i = 0;

        while (extracted == null && stacks.size() > i) {
            FluidStack stack = stacks.get(i++);

            extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, Action.PERFORM);
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = network.insertFluid(extracted, extracted.amount, Action.PERFORM);

        if (remainder != null) {
            storage.insert(remainder, remainder.amount, Action.PERFORM);
        }
    }

    private boolean isFluidDiskDone(IStorageDisk<FluidStack> storage, int slot) {
        if (ioMode == IO_MODE_INSERT && storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        //In Extract mode, we just need to check if the disk is full or not.
        if (ioMode == IO_MODE_EXTRACT)
            if (storage.getStored() == storage.getCapacity()) {
                moveDriveToOutput(slot);
                return true;
            } else {
                return false;
            }

        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());
        for (int i = 0; i < stacks.size(); ++i) {
            FluidStack stack = stacks.get(i);

            FluidStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, Action.SIMULATE);
            if (extracted == null) {
                continue;
            }

            FluidStack remainder = network.insertFluid(extracted, extracted.amount, Action.SIMULATE);
            if (remainder == null) { // A fluid could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }
        return true;
    }

    private void extractFluidFromNetwork(IStorageDisk<FluidStack> storage, int slot) {
        FluidStack extracted = null;
        int i = 0;

        if (fluidFilters.isEmpty()) {
            FluidStack toExtract = null;
            ArrayList<FluidStack> networkFluids = new ArrayList<>(network.getFluidStorageCache().getList().getStacks());

            int j = 0;

            while ((toExtract == null || toExtract.amount == 0) && j < networkFluids.size()) {
                toExtract = networkFluids.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractFluid(toExtract, upgrades.getItemInteractCount(), compare, Action.PERFORM);
            }
        } else {
            while (fluidFilters.getSlots() > i && extracted == null) {
                FluidStack filterStack = null;

                while (fluidFilters.getSlots() > i && filterStack == null) {
                    filterStack = fluidFilters.getFluid(i++);
                }

                if (filterStack != null) {
                    extracted = network.extractFluid(filterStack, upgrades.getItemInteractCount(), compare, Action.PERFORM);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = storage.insert(extracted, extracted.amount, Action.PERFORM);

        if (remainder != null) {
            network.insertFluid(remainder, remainder.amount, Action.PERFORM);
        }
    }

    private void moveDriveToOutput(int slot) {
        ItemStack disk = inputDisks.getStackInSlot(slot);
        if (!disk.isEmpty()) {
            int i = 0;
            while (i < 3 && !outputDisks.getStackInSlot(i).isEmpty()) {
                i++;
            }

            if (i == 3) {
                return;
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
        return world.isRemote ? TileDiskManipulator.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getMode() {
        return this.mode;
    }

    public int getIoMode() {
        return ioMode;
    }

    public void setIoMode(int ioMode) {
        this.ioMode = ioMode;
    }

    public IItemHandler getInputDisks() {
        return inputDisks;
    }

    public IItemHandler getOutputDisks() {
        return outputDisks;
    }

    public ItemHandlerProxy getDisks() {
        return disks;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IStorageDisk[] getItemDisks() {
        return itemDisks;
    }

    public IStorageDisk[] getFluidDisks() {
        return fluidDisks;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 3, tag);
        StackUtils.readItems(inputDisks, 4, tag);
        StackUtils.readItems(outputDisks, 5, tag);

        if (API.instance().getOneSixMigrationHelper().migrateDiskInventory(world, inputDisks)) {
            markDirty();
        }

        if (API.instance().getOneSixMigrationHelper().migrateDiskInventory(world, outputDisks)) {
            markDirty();
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 3, tag);
        StackUtils.writeItems(inputDisks, 4, tag);
        StackUtils.writeItems(outputDisks, 5, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);
        tag.setInteger(NBT_IO_MODE, ioMode);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 1, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
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

        if (tag.hasKey(NBT_IO_MODE)) {
            ioMode = tag.getInteger(NBT_IO_MODE);
        }

        OneSixMigrationHelper.migrateEmptyWhitelistToEmptyBlacklist(version, this, itemFilters);
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(inputDisks, outputDisks, upgrades);
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.inventory.*;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NetworkNodeDiskManipulator extends NetworkNode implements IComparable, IFilterable, IType {
    public static final String ID = "disk_manipulator";

    public static final int IO_MODE_INSERT = 0;
    public static final int IO_MODE_EXTRACT = 1;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_IO_MODE = "IOMode";

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private int ioMode = IO_MODE_INSERT;

    private IStorageDisk<ItemStack>[] itemStorages = new IStorageDisk[6];
    private IStorageDisk<FluidStack>[] fluidStorages = new IStorageDisk[6];

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK) {
        @Override
        public int getItemInteractCount() {
            int count = super.getItemInteractCount();

            if (type == IType.FLUIDS) {
                count *= Fluid.BUCKET_VOLUME;
            }

            return count;
        }
    };

    private ItemHandlerBase inputDisks = new ItemHandlerBase(3, new ItemHandlerListenerNetworkNode(this), NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    getStackInSlot(slot),
                    slot,
                    itemStorages,
                    fluidStorages,
                    s -> new StorageItemDiskManipulator(NetworkNodeDiskManipulator.this, s),
                    s -> new StorageFluidDiskManipulator(NetworkNodeDiskManipulator.this, s)
                );

                WorldUtils.updateBlock(world, pos);
            }
        }

        @Override
        @Nonnull
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

    private ItemHandlerBase outputDisks = new ItemHandlerBase(3, new ItemHandlerListenerNetworkNode(this), NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    getStackInSlot(slot),
                    3 + slot,
                    itemStorages,
                    fluidStorages,
                    s -> new StorageItemDiskManipulator(NetworkNodeDiskManipulator.this, s),
                    s -> new StorageFluidDiskManipulator(NetworkNodeDiskManipulator.this, s)
                );

                WorldUtils.updateBlock(world, pos);
            }
        }
    };

    private ItemHandlerProxy disks = new ItemHandlerProxy(inputDisks, outputDisks);

    public NetworkNodeDiskManipulator(World world, BlockPos pos) {
        super(world, pos);
    }

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

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
            while (slot < 3 && (itemStorages[slot] == null || checkItemDiskDone(itemStorages[slot], slot))) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            IStorageDisk<ItemStack> storage = itemStorages[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertItemIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractItemFromNetwork(storage, slot);
            }
        } else if (type == IType.FLUIDS) {
            while (slot < 3 && (fluidStorages[slot] == null || checkFluidDiskDone(fluidStorages[slot], slot))) {
                slot++;
            }

            if (slot == 3) {
                return;
            }

            IStorageDisk<FluidStack> storage = fluidStorages[slot];

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

            ItemStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, false);
            if (extracted == null) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), false);
            if (remainder == null) {
                break;
            }

            // We need to check if the stack was inserted
            storage.insert(((extracted == remainder) ? remainder.copy() : remainder), remainder.getCount(), false);
        }

        if (storage.getStacks().size() == 0) {
            moveDriveToOutput(slot);
        }
    }

    //Iterate through disk stacks, if none can be inserted, return that it is done processing and can be output.
    private boolean checkItemDiskDone(IStorageDisk<ItemStack> storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        List<ItemStack> stacks = new ArrayList<>(storage.getStacks());
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get(i);

            ItemStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, true);
            if (extracted == null) {
                continue;
            }

            ItemStack remainder = network.insertItem(extracted, extracted.getCount(), true);
            if (remainder == null) { //An item could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }
        return true;
    }

    private void extractItemFromNetwork(IStorageDisk<ItemStack> storage, int slot) {
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

            while ((toExtract == null || toExtract.isEmpty()) && j < networkItems.size()) {
                toExtract = networkItems.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractItem(toExtract, upgrades.getItemInteractCount(), compare, false);
            }
        } else {
            while (itemFilters.getSlots() > i && extracted == null) {
                ItemStack filterStack = ItemStack.EMPTY;

                while (itemFilters.getSlots() > i && filterStack.isEmpty()) {
                    filterStack = itemFilters.getStackInSlot(i++);
                }

                if (!filterStack.isEmpty()) {
                    extracted = network.extractItem(filterStack, upgrades.getItemInteractCount(), compare, false);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        ItemStack remainder = storage.insert(extracted, extracted.getCount(), false);

        if (remainder != null) {
            network.insertItem(remainder, remainder.getCount(), false);
        }
    }

    private void insertFluidIntoNetwork(IStorageDisk<FluidStack> storage, int slot) {
        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());

        FluidStack extracted = null;
        int i = 0;

        while (extracted == null && stacks.size() > i) {
            FluidStack stack = stacks.get(i++);

            extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, false);
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = network.insertFluid(extracted, extracted.amount, false);

        if (remainder != null) {
            storage.insert(remainder, remainder.amount, false);
        }
    }

    private boolean checkFluidDiskDone(IStorageDisk<FluidStack> storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return true;
        }

        List<FluidStack> stacks = new ArrayList<>(storage.getStacks());
        for (int i = 0; i < stacks.size(); ++i) {
            FluidStack stack = stacks.get(i);

            FluidStack extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, true);
            if (extracted == null) {
                continue;
            }

            FluidStack remainder = network.insertFluid(extracted, extracted.amount, true);
            if (remainder == null) { //A fluid could be inserted (no remainders when trying to). This disk isn't done.
                return false;
            }
        }
        return true;
    }

    private void extractFluidFromNetwork(IStorageDisk<FluidStack> storage, int slot) {
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
                extracted = network.extractFluid(toExtract, upgrades.getItemInteractCount(), compare, false);
            }
        } else {
            while (fluidFilters.getSlots() > i && extracted == null) {
                FluidStack filterStack = null;

                while (fluidFilters.getSlots() > i && filterStack == null) {
                    filterStack = fluidFilters.getFluidStackInSlot(i++);
                }

                if (filterStack != null) {
                    extracted = network.extractFluid(filterStack, upgrades.getItemInteractCount(), compare, false);
                }
            }
        }

        if (extracted == null) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack remainder = storage.insert(extracted, extracted.amount, false);

        if (remainder != null) {
            network.insertFluid(remainder, remainder.amount, false);
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
        return world.isRemote ? TileDiskManipulator.TYPE.getValue() : type;
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

    public ItemHandlerBase getItemFilters() {
        return itemFilters;
    }

    public ItemHandlerFluid getFluidFilters() {
        return fluidFilters;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IStorageDisk[] getItemStorages() {
        return itemStorages;
    }

    public IStorageDisk[] getFluidStorages() {
        return fluidStorages;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 3, tag);
        StackUtils.readItems(inputDisks, 4, tag);
        StackUtils.readItems(outputDisks, 5, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        onBreak();

        StackUtils.writeItems(upgrades, 3, tag);
        StackUtils.writeItems(inputDisks, 4, tag);
        StackUtils.writeItems(outputDisks, 5, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);
        StackUtils.writeItems(fluidFilters, 2, tag);

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
        StackUtils.readItems(fluidFilters, 2, tag);

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

    public void onBreak() {
        for (IStorageDisk storage : itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (IStorageDisk storage : fluidStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(inputDisks, outputDisks, upgrades);
    }
}

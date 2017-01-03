package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageFluidNBT;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageItemNBT;
import com.raoulvdberge.refinedstorage.block.EnumFluidStorageType;
import com.raoulvdberge.refinedstorage.block.EnumItemStorageType;
import com.raoulvdberge.refinedstorage.inventory.*;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

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

    private StorageItem[] itemStorages = new StorageItem[6];
    private StorageFluid[] fluidStorages = new StorageFluid[6];

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private ItemHandlerBasic inputDisks = new ItemHandlerBasic(3, new ItemHandlerListenerNetworkNode(this), IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                RSUtils.createStorages(getStackInSlot(slot), slot, itemStorages, fluidStorages, s -> new StorageItem(s), s -> new StorageFluid(s));

                RSUtils.updateBlock(holder.world(), holder.pos());
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

    private ItemHandlerBasic outputDisks = new ItemHandlerBasic(3, new ItemHandlerListenerNetworkNode(this), IItemValidator.STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                RSUtils.createStorages(getStackInSlot(slot), 3 + slot, itemStorages, fluidStorages, s -> new StorageItem(s), s -> new StorageFluid(s));

                RSUtils.updateBlock(holder.world(), holder.pos());
            }
        }
    };

    public NetworkNodeDiskManipulator(INetworkNodeHolder holder) {
        super(holder);
    }

    public class StorageItem extends StorageItemNBT {
        private int lastState;

        public StorageItem(ItemStack disk) {
            super(disk.getTagCompound(), EnumItemStorageType.getById(disk.getItemDamage()).getCapacity(), NetworkNodeDiskManipulator.this);

            lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean isVoiding() {
            return false;
        }

        @Override
        public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insert(stack, size, simulate);
        }

        @Override
        public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return null;
            }

            return super.extract(stack, size, flags, simulate);
        }

        @Override
        public void onStorageChanged() {
            super.onStorageChanged();

            int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

            if (lastState != currentState) {
                lastState = currentState;

                RSUtils.updateBlock(holder.world(), holder.pos());
            }
        }
    }

    public class StorageFluid extends StorageFluidNBT {
        private int lastState;

        public StorageFluid(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), NetworkNodeDiskManipulator.this);

            lastState = TileDiskDrive.getDiskState(getStored(), getCapacity());
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean isVoiding() {
            return false;
        }

        @Override
        @Nullable
        public FluidStack insert(@Nonnull FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return RSUtils.copyStackWithSize(stack, size);
            }

            return super.insert(stack, size, simulate);
        }

        @Override
        @Nullable
        public FluidStack extract(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return null;
            }

            return super.extract(stack, size, flags, simulate);
        }

        @Override
        public void onStorageChanged() {
            super.onStorageChanged();

            int currentState = TileDiskDrive.getDiskState(getStored(), getCapacity());

            if (lastState != currentState) {
                lastState = currentState;

                RSUtils.updateBlock(holder.world(), holder.pos());
            }
        }
    }

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, new ItemHandlerListenerNetworkNode(this));
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

        if (network == null || ticks % upgrades.getSpeed() != 0) {
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

            StorageItem storage = itemStorages[slot];

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

            StorageFluid storage = fluidStorages[slot];

            if (ioMode == IO_MODE_INSERT) {
                insertIntoNetwork(storage, slot);
            } else if (ioMode == IO_MODE_EXTRACT) {
                extractFromNetwork(storage, slot);
            }
        }
    }

    private void insertIntoNetwork(StorageItem storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }

        for (int i = 0; i < storage.getStacks().size(); i++) {
            ItemStack stack = storage.getStacks().get(i);
            if (stack == null) {
                continue;
            }

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

    private void extractFromNetwork(StorageItem storage, int slot) {
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

            while ((toExtract == null || toExtract.getCount() == 0) && j < networkItems.size()) {
                toExtract = networkItems.get(j++);
            }

            if (toExtract != null) {
                extracted = network.extractItem(toExtract, upgrades.getItemInteractCount(), compare, false);
            }
        } else {
            while (itemFilters.getSlots() > i && extracted == null) {
                ItemStack stack = null;

                while (itemFilters.getSlots() > i && stack == null) {
                    stack = itemFilters.getStackInSlot(i++);
                }

                if (stack != null) {
                    extracted = network.extractItem(stack, upgrades.getItemInteractCount(), compare, false);
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

    private void insertIntoNetwork(StorageFluid storage, int slot) {
        if (storage.getStored() == 0) {
            moveDriveToOutput(slot);
            return;
        }

        FluidStack extracted = null;
        int i = 0;

        while (extracted == null && storage.getStacks().size() > i) {
            FluidStack stack = storage.getStacks().get(i);

            while (stack == null && storage.getStacks().size() > i) {
                i++;
            }

            if (stack != null) {
                extracted = storage.extract(stack, upgrades.getItemInteractCount(), compare, false);
            }
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

    private void extractFromNetwork(StorageFluid storage, int slot) {
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
                FluidStack stack = null;

                while (fluidFilters.getSlots() > i && stack == null) {
                    stack = fluidFilters.getFluidStackInSlot(i++);
                }

                if (stack != null) {
                    extracted = network.extractFluid(stack, upgrades.getItemInteractCount(), compare, false);
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

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public StorageItem[] getItemStorages() {
        return itemStorages;
    }

    public StorageFluid[] getFluidStorages() {
        return fluidStorages;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(upgrades, 3, tag);
        RSUtils.readItems(inputDisks, 4, tag);
        RSUtils.readItems(outputDisks, 5, tag);
    }

    @Override
    public String getId() {
        return ID;
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

    public void onBreak() {
        for (StorageItem storage : itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (StorageFluid storage : fluidStorages) {
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

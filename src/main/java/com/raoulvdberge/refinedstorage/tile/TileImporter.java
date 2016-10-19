package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import mcmultipart.microblock.IMicroblock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileImporter extends TileMultipartNode implements IComparable, IFilterable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;

    private int currentSlot;

    public TileImporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.importerUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (type == IType.ITEMS) {
            IItemHandler handler = RSUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

            if (getFacingTile() instanceof TileDiskDrive || handler == null) {
                return;
            }

            if (currentSlot >= handler.getSlots()) {
                currentSlot = 0;
            }

            if (handler.getSlots() > 0) {
                ItemStack stack = handler.getStackInSlot(currentSlot);

                if (stack == null || !IFilterable.canTake(itemFilters, mode, compare, stack)) {
                    currentSlot++;
                } else if (ticks % upgrades.getSpeed() == 0) {
                    ItemStack result = handler.extractItem(currentSlot, upgrades.getInteractStackSize(), true);

                    if (result != null && network.insertItem(result, result.stackSize, true) == null) {
                        network.insertItem(result, result.stackSize, false);

                        handler.extractItem(currentSlot, upgrades.getInteractStackSize(), false);
                    } else {
                        currentSlot++;
                    }
                }
            }
        } else if (type == IType.FLUIDS && ticks % upgrades.getSpeed() == 0) {
            IFluidHandler handler = RSUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());

            if (handler != null) {
                FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                if (stack != null && IFilterable.canTakeFluids(fluidFilters, mode, compare, stack) && network.insertFluid(stack, stack.amount, true) == null) {
                    FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME * upgrades.getInteractStackSize(), true);

                    if (drained != null) {
                        network.insertFluid(drained, drained.amount, false);
                    }
                }
            }
        }
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        RSUtils.readItems(itemFilters, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
        RSUtils.readItems(fluidFilters, 2, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        RSUtils.writeItems(itemFilters, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);
        RSUtils.writeItems(fluidFilters, 2, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
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
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) upgrades;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.block.DetectorBlock;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.tile.DetectorTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.RedstoneMode;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class DetectorNetworkNode extends NetworkNode implements IComparable, IType {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "detector");

    private static final int SPEED = 5;

    public static final int MODE_UNDER = 0;
    public static final int MODE_EQUAL = 1;
    public static final int MODE_ABOVE = 2;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private final BaseItemHandler itemFilters = new BaseItemHandler(1).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(1).addListener(new NetworkNodeFluidInventoryListener(this));

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;
    private int mode = MODE_EQUAL;
    private int amount = 0;

    private boolean powered = false;
    private boolean wasPowered;

    public DetectorNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getDetector().getUsage();
    }

    @Override
    public void update() {
        super.update();

        if (powered != wasPowered && world.isLoaded(pos)) {
            wasPowered = powered;

            world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(DetectorBlock.POWERED, powered));
            world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
        }

        if (canUpdate() && ticks % SPEED == 0) {
            if (type == IType.ITEMS) {
                ItemStack slot = itemFilters.getStackInSlot(0);

                if (!slot.isEmpty()) {
                    ItemStack stack = network.getItemStorageCache().getList().get(slot, compare);

                    powered = isPowered(stack == null ? null : stack.getCount());
                } else {
                    powered = isPowered(network.getItemStorageCache().getList().getStacks().stream().map(e -> e.getStack().getCount()).mapToInt(Number::intValue).sum());
                }
            } else if (type == IType.FLUIDS) {
                FluidStack slot = fluidFilters.getFluid(0);

                if (!slot.isEmpty()) {
                    FluidStack stack = network.getFluidStorageCache().getList().get(slot, compare);

                    powered = isPowered(stack == null ? null : stack.getAmount());
                } else {
                    powered = isPowered(network.getFluidStorageCache().getList().getStacks().stream().map(e -> e.getStack().getAmount()).mapToInt(Number::intValue).sum());
                }
            }
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        if (!state) {
            powered = false;
        }
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    private boolean isPowered(@Nullable Integer size) {
        if (size != null) {
            switch (mode) {
                case MODE_UNDER:
                    return size < amount;
                case MODE_EQUAL:
                    return size == amount;
                case MODE_ABOVE:
                    return size > amount;
                default:
                    return false;
            }
        } else {
            if (mode == MODE_UNDER && amount != 0) {
                return true;
            }

            return mode == MODE_EQUAL && amount == 0;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_AMOUNT, amount);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        if (tag.contains(NBT_AMOUNT)) {
            amount = tag.getInt(NBT_AMOUNT);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }

    @Override
    public int getType() {
        return world.isClientSide ? DetectorTile.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeDetector extends NetworkNode implements IComparable, IType {
    public static final String ID = "detector";

    private static final int SPEED = 5;

    public static final int MODE_UNDER = 0;
    public static final int MODE_EQUAL = 1;
    public static final int MODE_ABOVE = 2;
    public static final int MODE_AUTOCRAFTING = 3;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private ItemHandlerBase itemFilters = new ItemHandlerBase(1, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(1, new ListenerNetworkNode(this));

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private int mode = MODE_EQUAL;
    private int amount = 0;

    private boolean powered = false;
    private boolean wasPowered;

    public NetworkNodeDetector(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.detectorUsage;
    }

    @Override
    public void update() {
        super.update();

        if (powered != wasPowered) {
            wasPowered = powered;

            world.notifyNeighborsOfStateChange(pos, RSBlocks.DETECTOR, true);

            WorldUtils.updateBlock(world, pos);
        }

        if (network != null && canUpdate() && ticks % SPEED == 0) {
            if (type == IType.ITEMS) {
                ItemStack slot = itemFilters.getStackInSlot(0);

                if (!slot.isEmpty()) {
                    if (mode == MODE_AUTOCRAFTING) {
                        boolean found = false;

                        for (ICraftingTask task : network.getCraftingManager().getTasks()) {
                            for (ItemStack output : task.getPattern().getOutputs()) {
                                if (API.instance().getComparer().isEqualNoQuantity(slot, output)) {
                                    found = true;

                                    break;
                                }
                            }

                            if (found) {
                                break;
                            }
                        }

                        powered = found;
                    } else {
                        ItemStack stack = network.getItemStorageCache().getList().get(slot, compare);

                        powered = isPowered(stack == null ? null : stack.getCount());
                    }
                } else {
                    powered = mode == MODE_AUTOCRAFTING ? !network.getCraftingManager().getTasks().isEmpty() : isPowered(network.getItemStorageCache().getList().getStacks().stream().map(ItemStack::getCount).mapToInt(Number::intValue).sum());
                }
            } else if (type == IType.FLUIDS) {
                FluidStack slot = fluidFilters.getFluid(0);

                if (slot != null) {
                    FluidStack stack = network.getFluidStorageCache().getList().get(slot, compare);

                    powered = isPowered(stack == null ? null : stack.amount);
                } else {
                    powered = isPowered(network.getFluidStorageCache().getList().getStacks().stream().map(s -> s.amount).mapToInt(Number::intValue).sum());
                }
            }
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

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

    private boolean isPowered(Integer size) {
        if (size != null) {
            switch (mode) {
                case MODE_UNDER:
                    return size < amount;
                case MODE_EQUAL:
                    return size == amount;
                case MODE_ABOVE:
                    return size > amount;
            }
        } else {
            if (mode == MODE_UNDER && amount != 0) {
                return true;
            } else if (mode == MODE_EQUAL && amount == 0) {
                return true;
            } else {
                return false;
            }
        }

        return false;
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
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_AMOUNT, amount);
        tag.setInteger(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_AMOUNT)) {
            amount = tag.getInteger(NBT_AMOUNT);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
        }
    }

    public IItemHandler getInventory() {
        return itemFilters;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }

    @Override
    public int getType() {
        return world.isRemote ? TileDetector.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }
}

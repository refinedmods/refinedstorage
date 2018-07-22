package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.*;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

// TODO: Crafting upgrade
public class NetworkNodeFluidInterface extends NetworkNode implements IComparable {
    public static final String ID = "fluid_interface";

    public static final int TANK_CAPACITY = 16000;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TANK_IN = "TankIn";
    private static final String NBT_TANK_OUT = "TankOut";

    private int compare = IComparer.COMPARE_NBT;

    private FluidTank tankIn = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (!world.isRemote) {
                ((TileFluidInterface) world.getTileEntity(pos)).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_IN);
            }

            markDirty();
        }
    };

    private FluidTank tankOut = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (!world.isRemote) {
                ((TileFluidInterface) world.getTileEntity(pos)).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_OUT);
            }

            markDirty();
        }
    };

    private FluidHandlerFluidInterface tank = new FluidHandlerFluidInterface(tankIn, tankOut);

    private ItemHandlerBase in = new ItemHandlerBase(1, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid out = new ItemHandlerFluid(1, new ItemHandlerListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    public NetworkNodeFluidInterface(World world, BlockPos pos) {
        super(world, pos);

        tankIn.setCanDrain(false);
        tankIn.setCanFill(true);

        tankOut.setCanDrain(true);
        tankOut.setCanFill(false);
    }

    @Override
    public void update() {
        super.update();

        ItemStack container = in.getStackInSlot(0);

        if (!container.isEmpty()) {
            Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

            if (result.getValue() != null && tankIn.fillInternal(result.getValue(), false) == result.getValue().amount) {
                result = StackUtils.getFluid(container, false);

                tankIn.fillInternal(result.getValue(), true);

                in.setStackInSlot(0, result.getLeft());
            }
        }

        if (network != null && canUpdate() && ticks % upgrades.getSpeed() == 0) {
            FluidStack drained = tankIn.drainInternal(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), true);

            // Drain in tank
            if (drained != null) {
                FluidStack remainder = network.insertFluidTracked(drained, drained.amount);

                if (remainder != null) {
                    tankIn.fillInternal(remainder, true);
                }
            }

            FluidStack stack = out.getFluidStackInSlot(0);

            // Fill out tank

            // If our out fluid doesn't match the new fluid, empty it first
            if (tankOut.getFluid() != null && (stack == null || (tankOut.getFluid().getFluid() != stack.getFluid()))) {
                FluidStack remainder = tankOut.drainInternal(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), true);

                if (remainder != null) {
                    network.insertFluidTracked(remainder, remainder.amount);
                }
            } else if (stack != null) {
                // Fill the out fluid
                FluidStack stackInStorage = network.getFluidStorageCache().getList().get(stack, compare);

                if (stackInStorage != null) {
                    int toExtract = Math.min(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), stackInStorage.amount);

                    int spaceRemaining = tankOut.getCapacity() - tankOut.getFluidAmount();
                    if (toExtract > spaceRemaining) {
                        toExtract = spaceRemaining;
                    }
                    if (toExtract <= 0) {
                        return;
                    }

                    FluidStack took = network.extractFluid(stack, toExtract, compare, Action.SIMULATE);

                    if (took != null && (toExtract - tankOut.fillInternal(took, false)) == 0) {
                        took = network.extractFluid(stack, toExtract, compare, Action.PERFORM);

                        tankOut.fillInternal(took, true);
                    }
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.fluidInterfaceUsage;
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
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 0, tag);
        StackUtils.writeItems(in, 1, tag);

        tag.setTag(NBT_TANK_IN, tankIn.writeToNBT(new NBTTagCompound()));
        tag.setTag(NBT_TANK_OUT, tankOut.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 0, tag);
        StackUtils.readItems(in, 1, tag);

        if (tag.hasKey(NBT_TANK_IN)) {
            tankIn.readFromNBT(tag.getCompoundTag(NBT_TANK_IN));
        }

        if (tag.hasKey(NBT_TANK_OUT)) {
            tankOut.readFromNBT(tag.getCompoundTag(NBT_TANK_OUT));
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(out, 2, tag);

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(out, 2, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    public ItemHandlerBase getIn() {
        return in;
    }

    public ItemHandlerFluid getOut() {
        return out;
    }

    public FluidHandlerFluidInterface getTank() {
        return tank;
    }

    public FluidTank getTankIn() {
        return tankIn;
    }

    public FluidTank getTankOut() {
        return tankOut;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(in, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

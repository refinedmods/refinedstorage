package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.*;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.apache.commons.lang3.tuple.Pair;

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

            if (!holder.world().isRemote) {
                ((TileFluidInterface) holder.world().getTileEntity(holder.pos())).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_IN);
            }

            markDirty();
        }
    };

    private FluidTank tankOut = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (!holder.world().isRemote) {
                ((TileFluidInterface) holder.world().getTileEntity(holder.pos())).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_OUT);
            }

            markDirty();
        }
    };

    private FluidHandlerFluidInterface tank = new FluidHandlerFluidInterface(tankIn, tankOut);

    private ItemHandlerBasic in = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid out = new ItemHandlerFluid(1, new ItemHandlerListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    public NetworkNodeFluidInterface(INetworkNodeHolder holder) {
        super(holder);

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
            Pair<ItemStack, FluidStack> result = RSUtils.getFluidFromStack(container, true);

            if (result.getValue() != null && tankIn.fillInternal(result.getValue(), false) == result.getValue().amount) {
                result = RSUtils.getFluidFromStack(container, false);

                tankIn.fillInternal(result.getValue(), true);

                in.setStackInSlot(0, result.getLeft());
            }
        }

        if (network != null && ticks % upgrades.getSpeed() == 0) {
            FluidStack drained = tankIn.drainInternal(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), true);

            // Drain in tank
            if (drained != null) {
                FluidStack remainder = network.insertFluid(drained, drained.amount, false);

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
                    network.insertFluid(remainder, remainder.amount, false);
                }
            } else if (stack != null) {
                // Fill the out fluid
                FluidStack stackInStorage = network.getFluidStorageCache().getList().get(stack, compare);

                if (stackInStorage != null) {
                    int toExtract = Math.min(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), stackInStorage.amount);

                    FluidStack took = network.extractFluid(stack, toExtract, compare, true);

                    if (took != null && (toExtract - tankOut.fillInternal(took, false)) == 0) {
                        took = network.extractFluid(stack, toExtract, compare, false);

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

        RSUtils.writeItems(upgrades, 0, tag);
        RSUtils.writeItems(in, 1, tag);

        tag.setTag(NBT_TANK_IN, tankIn.writeToNBT(new NBTTagCompound()));
        tag.setTag(NBT_TANK_OUT, tankOut.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(upgrades, 0, tag);
        RSUtils.readItems(in, 1, tag);

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

        RSUtils.writeItems(out, 2, tag);

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        RSUtils.readItems(out, 2, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    public ItemHandlerBasic getIn() {
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
    public boolean hasConnectivityState() {
        return true;
    }
}

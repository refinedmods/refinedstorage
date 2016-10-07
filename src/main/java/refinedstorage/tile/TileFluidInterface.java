package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import refinedstorage.RS;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RSSerializers;
import refinedstorage.tile.data.TileDataParameter;

public class TileFluidInterface extends TileNode implements IComparable {
    public static final int TANK_CAPACITY = 16000;

    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();

    public static final TileDataParameter<FluidStack> TANK_IN = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, null, new ITileDataProducer<FluidStack, TileFluidInterface>() {
        @Override
        public FluidStack getValue(TileFluidInterface tile) {
            return tile.tankIn.getFluid();
        }
    });

    public static final TileDataParameter<FluidStack> TANK_OUT = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, null, new ITileDataProducer<FluidStack, TileFluidInterface>() {
        @Override
        public FluidStack getValue(TileFluidInterface tile) {
            return tile.tankOut.getFluid();
        }
    });

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TANK_IN = "TankIn";
    private static final String NBT_TANK_OUT = "TankOut";

    private int compare = CompareUtils.COMPARE_NBT;

    private FluidTank tankIn = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (worldObj != null && !worldObj.isRemote) {
                dataManager.sendParameterToWatchers(TANK_IN);
            }

            markDirty();
        }
    };

    private FluidTank tankOut = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (worldObj != null && !worldObj.isRemote) {
                dataManager.sendParameterToWatchers(TANK_OUT);
            }

            markDirty();
        }
    };

    private ItemHandlerBasic in = new ItemHandlerBasic(1, this);
    private ItemHandlerFluid out = new ItemHandlerFluid(1, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    public TileFluidInterface() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addParameter(TANK_IN);
        dataManager.addParameter(TANK_OUT);

        tankIn.setCanDrain(false);
        tankIn.setCanFill(true);

        tankOut.setCanDrain(true);
        tankOut.setCanFill(false);
    }

    @Override
    public void updateNode() {
        ItemStack container = in.getStackInSlot(0);

        if (container != null) {
            FluidStack fluid = FluidUtils.getFluidFromStack(container, true);

            if (fluid != null && tankIn.fillInternal(fluid, false) == fluid.amount) {
                tankIn.fillInternal(FluidUtils.getFluidFromStack(container, false), true);
            }
        }

        if (ticks % upgrades.getSpeed() == 0) {
            FluidStack drained = tankIn.drainInternal(Fluid.BUCKET_VOLUME, true);

            if (drained != null) {
                FluidStack remainder = network.insertFluid(drained, drained.amount, false);

                if (remainder != null) {
                    tankIn.fillInternal(remainder, true);
                }
            }

            FluidStack stack = out.getFluidStackInSlot(0);

            if (tankOut.getFluid() != null && (stack == null || (tankOut.getFluid().getFluid() != stack.getFluid()))) {
                FluidStack remainder = tankOut.drainInternal(Fluid.BUCKET_VOLUME, true);

                if (remainder != null) {
                    network.insertFluid(remainder, remainder.amount, false);
                }
            } else if (stack != null) {
                FluidStack stackInStorage = network.getFluidStorage().get(stack, compare);

                if (stackInStorage != null) {
                    int toExtract = Math.min(Fluid.BUCKET_VOLUME, stackInStorage.amount);

                    FluidStack took = network.extractFluid(stack, toExtract, compare);

                    if (took != null) {
                        int remainder = toExtract - tankOut.fillInternal(took, true);

                        if (remainder > 0) {
                            network.insertFluid(took, remainder, false);
                        }
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

        writeItems(upgrades, 0, tag);
        writeItems(in, 1, tag);
        writeItems(out, 2, tag);

        tag.setTag(NBT_TANK_IN, tankIn.writeToNBT(new NBTTagCompound()));
        tag.setTag(NBT_TANK_OUT, tankOut.writeToNBT(new NBTTagCompound()));

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(upgrades, 0, tag);
        readItems(in, 1, tag);
        readItems(out, 2, tag);

        if (tag.hasKey(NBT_TANK_IN)) {
            tankIn.readFromNBT(tag.getCompoundTag(NBT_TANK_IN));
        }

        if (tag.hasKey(NBT_TANK_OUT)) {
            tankOut.readFromNBT(tag.getCompoundTag(NBT_TANK_OUT));
        }

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

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return facing == EnumFacing.DOWN ? (T) tankOut : (T) tankIn;
        }

        return super.getCapability(capability, facing);
    }
}

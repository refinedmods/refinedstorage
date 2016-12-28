package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.tile.IReaderWriter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class ReaderWriterHandlerFluids implements IReaderWriterHandler {
    public static final String ID = "fluids";

    private FluidTank tank;
    private FluidTankReaderWriter tankReader, tankWriter;

    public ReaderWriterHandlerFluids(@Nullable NBTTagCompound tag) {
        this.tank = new FluidTank(4 * Fluid.BUCKET_VOLUME);
        this.tankReader = new FluidTankReaderWriter(tank, true, false);
        this.tankWriter = new FluidTankReaderWriter(tank, false, true);

        if (tag != null) {
            this.tank.readFromNBT(tag);
        }
    }

    @Override
    public void update(IReaderWriterChannel channel) {
        // NO OP
    }

    @Override
    public void onWriterDisabled(IWriter writer) {
        // NO OP
    }

    @Override
    public boolean hasCapability(IReaderWriter readerWriter, Capability<?> capability) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (readerWriter instanceof IReader || readerWriter instanceof IWriter);
    }

    @Override
    public <T> T getCapability(IReaderWriter readerWriter, Capability<T> capability) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (readerWriter instanceof IReader) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankReader);
            } else if (readerWriter instanceof IWriter) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankWriter);
            }
        }

        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tank.writeToNBT(tag);

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    private class FluidTankReaderWriter implements IFluidTank, IFluidHandler {
        private FluidTank parent;
        private boolean canFill, canDrain;
        private IFluidTankProperties[] properties;

        public FluidTankReaderWriter(FluidTank parent, boolean canFill, boolean canDrain) {
            this.parent = parent;

            this.canFill = canFill;
            this.canDrain = canDrain;

            this.properties = new IFluidTankProperties[]{
                new IFluidTankProperties() {
                    @Nullable
                    @Override
                    public FluidStack getContents() {
                        return parent.getFluid();
                    }

                    @Override
                    public int getCapacity() {
                        return parent.getCapacity();
                    }

                    @Override
                    public boolean canFill() {
                        return canFill;
                    }

                    @Override
                    public boolean canDrain() {
                        return canDrain;
                    }

                    @Override
                    public boolean canFillFluidType(FluidStack fluidStack) {
                        return canFill;
                    }

                    @Override
                    public boolean canDrainFluidType(FluidStack fluidStack) {
                        return canDrain;
                    }
                }
            };
        }

        @Nullable
        @Override
        public FluidStack getFluid() {
            return parent.getFluid();
        }

        @Override
        public int getFluidAmount() {
            return parent.getFluidAmount();
        }

        @Override
        public int getCapacity() {
            return parent.getCapacity();
        }

        @Override
        public FluidTankInfo getInfo() {
            return parent.getInfo();
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return properties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return canFill ? parent.fill(resource, doFill) : 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return canDrain ? parent.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return canDrain ? parent.drain(maxDrain, doDrain) : null;
        }
    }
}

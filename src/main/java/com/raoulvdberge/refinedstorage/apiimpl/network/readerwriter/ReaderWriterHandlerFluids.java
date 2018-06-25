package com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ReaderWriterHandlerFluids implements IReaderWriterHandler {
    public static final String ID = "fluids";

    private static final IFluidHandler NULL_CAP = new IFluidHandler() {
        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[0];
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    };

    private FluidTank tank;
    private FluidTankReaderWriter tankReader, tankWriter;

    public ReaderWriterHandlerFluids(@Nullable NBTTagCompound tag) {
        this.tank = new FluidTank(16 * Fluid.BUCKET_VOLUME);
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
    public boolean hasCapabilityReader(IReader reader, Capability<?> capability) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapabilityReader(IReader reader, Capability<T> capability) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankReader);
        }

        return null;
    }

    @Override
    public boolean hasCapabilityWriter(IWriter writer, Capability<?> capability) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapabilityWriter(IWriter writer, Capability<T> capability) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankWriter);
        }

        return null;
    }

    @Override
    public Object getNullCapability() {
        return NULL_CAP;
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        tank.writeToNBT(tag);

        return tag;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public List<ITextComponent> getStatusReader(IReader reader, IReaderWriterChannel channel) {
        return getStatus(tankReader);
    }

    @Override
    public List<ITextComponent> getStatusWriter(IWriter writer, IReaderWriterChannel channel) {
        return getStatus(tankWriter);
    }

    private List<ITextComponent> getStatus(IFluidTank tank) {
        FluidStack stack = tank.getFluid();

        if (stack == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new TextComponentString(API.instance().getQuantityFormatter().format(stack.amount) + " mB ").appendSibling(new TextComponentTranslation(stack.getUnlocalizedName())));
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

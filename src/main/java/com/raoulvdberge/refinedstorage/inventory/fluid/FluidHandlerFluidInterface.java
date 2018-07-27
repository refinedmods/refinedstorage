package com.raoulvdberge.refinedstorage.inventory.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class FluidHandlerFluidInterface implements IFluidHandler {
    private FluidTank input;
    private FluidTank output;
    private IFluidTankProperties[] properties;

    public FluidHandlerFluidInterface(FluidTank input, FluidTank output) {
        this.input = input;
        this.output = output;
        this.properties = new IFluidTankProperties[]{
            new FluidTankPropertiesWrapper(input),
            new FluidTankPropertiesWrapper(output)
        };
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return input.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return output.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return output.drain(maxDrain, doDrain);
    }
}

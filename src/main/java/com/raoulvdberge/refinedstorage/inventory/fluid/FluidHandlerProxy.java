package com.raoulvdberge.refinedstorage.inventory.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class FluidHandlerProxy implements IFluidHandler {
    private FluidTank insertHandler;
    private FluidTank extractHandler;
    private IFluidTankProperties[] properties;

    public FluidHandlerProxy(FluidTank insertHandler, FluidTank extractHandler) {
        this.insertHandler = insertHandler;
        this.extractHandler = extractHandler;
        this.properties = new IFluidTankProperties[]{
            new FluidTankPropertiesWrapper(insertHandler),
            new FluidTankPropertiesWrapper(extractHandler)
        };
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return insertHandler.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return extractHandler.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return extractHandler.drain(maxDrain, doDrain);
    }
}

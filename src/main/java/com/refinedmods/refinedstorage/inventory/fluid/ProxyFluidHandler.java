package com.refinedmods.refinedstorage.inventory.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class ProxyFluidHandler implements IFluidHandler {
    private final FluidTank insertHandler;
    private final FluidTank extractHandler;

    public ProxyFluidHandler(FluidTank insertHandler, FluidTank extractHandler) {
        this.insertHandler = insertHandler;
        this.extractHandler = extractHandler;
    }

    @Override
    public int getTanks() {
        return 2;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? insertHandler.getFluidInTank(0) : extractHandler.getFluidInTank(0);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? insertHandler.getTankCapacity(0) : extractHandler.getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 ? insertHandler.isFluidValid(0, stack) : extractHandler.isFluidValid(0, stack);

    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return insertHandler.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return extractHandler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return extractHandler.drain(maxDrain, action);
    }
}

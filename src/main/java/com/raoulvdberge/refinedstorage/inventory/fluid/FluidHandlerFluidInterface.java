package com.raoulvdberge.refinedstorage.inventory.fluid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.List;

public class FluidHandlerFluidInterface implements IFluidHandler {
    private static final IFluidTankProperties[] NO_PROPS = new IFluidTankProperties[0];

    private INetworkNode node;

    public FluidHandlerFluidInterface(INetworkNode node) {
        this.node = node;
    }

    @Nullable
    private INetwork getNetwork() {
        if (node.getNetwork() != null && node.canUpdate()) {
            return node.getNetwork();
        }

        return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        INetwork network = getNetwork();

        if (network == null) {
            return NO_PROPS;
        }

        List<FluidStack> stacks = network.getFluidStorageCache().getList().getStacks();

        if (stacks.isEmpty()) {
            return NO_PROPS;
        }

        IFluidTankProperties[] props = new IFluidTankProperties[stacks.size()];

        for (int i = 0; i < stacks.size(); ++i) {
            FluidStack stack = stacks.get(i);

            props[i] = new IFluidTankProperties() {
                @Nullable
                @Override
                public FluidStack getContents() {
                    return stack;
                }

                @Override
                public int getCapacity() {
                    return -1;
                }

                @Override
                public boolean canFill() {
                    return false;
                }

                @Override
                public boolean canDrain() {
                    return false;
                }

                @Override
                public boolean canFillFluidType(FluidStack fluidStack) {
                    return false;
                }

                @Override
                public boolean canDrainFluidType(FluidStack fluidStack) {
                    return false;
                }
            };
        }

        return props;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        INetwork network = getNetwork();

        if (network != null) {
            FluidStack remainder = network.insertFluid(resource, resource.amount, doFill ? Action.PERFORM : Action.SIMULATE);

            return remainder == null ? resource.amount : resource.amount - remainder.amount;
        }

        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        INetwork network = getNetwork();

        if (network != null) {
            return network.extractFluid(resource, resource.amount, doDrain ? Action.PERFORM : Action.SIMULATE);
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        INetwork network = getNetwork();

        if (network != null) {
            List<FluidStack> fluids = network.getFluidStorageCache().getList().getStacks();

            if (fluids.isEmpty()) {
                return null;
            }

            FluidStack firstFluid = fluids.get(0);

            return network.extractFluid(firstFluid, Math.min(firstFluid.amount, maxDrain), doDrain ? Action.PERFORM : Action.SIMULATE);
        }

        return null;
    }
}

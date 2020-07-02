package com.refinedmods.refinedstorage.apiimpl.network.node.iface.fluid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidInterfaceOutputInterceptor implements IOutputInterceptor {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid_interface");

    private final FluidStack interestedStack;
    private final DimensionType fluidInterfaceDimension;
    private final BlockPos fluidInterfacePos;

    public FluidInterfaceOutputInterceptor(FluidStack interestedStack, DimensionType fluidInterfaceDimension, BlockPos fluidInterfacePos) {
        this.interestedStack = interestedStack;
        this.fluidInterfaceDimension = fluidInterfaceDimension;
        this.fluidInterfacePos = fluidInterfacePos;
    }

    @Nullable
    private FluidInterfaceNetworkNode getFluidInterface(MinecraftServer server) {
        ServerWorld world = DimensionManager.getWorld(server, fluidInterfaceDimension, true, true);
        if (world == null) {
            return null;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(fluidInterfacePos);
        if (!(node instanceof FluidInterfaceNetworkNode)) {
            return null;
        }

        return (FluidInterfaceNetworkNode) node;
    }

    @Override
    public ItemStack intercept(MinecraftServer server, ItemStack stack) {
        return stack;
    }

    @Override
    public FluidStack intercept(MinecraftServer server, FluidStack stack) {
        if (API.instance().getComparer().isEqual(stack, interestedStack, IComparer.COMPARE_NBT)) {
            FluidInterfaceNetworkNode fluidInterface = getFluidInterface(server);
            if (fluidInterface == null) {
                return stack;
            }

            FluidStack wanted = fluidInterface.getOut().getFluid(0);
            if (!API.instance().getComparer().isEqual(stack, wanted, IComparer.COMPARE_NBT)) {
                return stack;
            }

            FluidStack got = fluidInterface.getTankOut().getFluid();

            int needed = wanted.getAmount() - got.getAmount();
            if (needed > stack.getAmount()) {
                needed = stack.getAmount();
            }

            if (needed > 0) {
                if (got.isEmpty()) {
                    fluidInterface.getTankOut().setFluid(StackUtils.copy(stack, needed));
                } else {
                    fluidInterface.getTankOut().getFluid().grow(needed);
                    fluidInterface.markDirty();
                }

                stack.shrink(needed);
            }
        }

        return stack;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("Fluid", interestedStack.writeToNBT(new CompoundNBT()));
        tag.putString("Dim", fluidInterfaceDimension.getRegistryName().toString());
        tag.putLong("Pos", fluidInterfacePos.toLong());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

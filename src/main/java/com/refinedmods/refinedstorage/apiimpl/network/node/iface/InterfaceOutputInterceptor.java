package com.refinedmods.refinedstorage.apiimpl.network.node.iface;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class InterfaceOutputInterceptor implements IOutputInterceptor {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "interface");

    private final ItemStack interestedItemStack;
    private final DimensionType interfaceDimension;
    private final BlockPos interfacePos;
    private final int slot;

    public InterfaceOutputInterceptor(ItemStack interestedItemStack, DimensionType interfaceDimension, BlockPos interfacePos, int slot) {
        this.interestedItemStack = interestedItemStack;
        this.interfaceDimension = interfaceDimension;
        this.interfacePos = interfacePos;
        this.slot = slot;
    }

    @Nullable
    private InterfaceNetworkNode getInterface(MinecraftServer server) {
        ServerWorld world = DimensionManager.getWorld(server, interfaceDimension, true, true);
        if (world == null) {
            return null;
        }

        INetworkNodeManager manager = API.instance().getNetworkNodeManager(world);

        INetworkNode node = manager.getNode(interfacePos);
        if (!(node instanceof InterfaceNetworkNode)) {
            return null;
        }

        return (InterfaceNetworkNode) node;
    }

    @Override
    public ItemStack intercept(MinecraftServer server, ItemStack stack) {
        if (API.instance().getComparer().isEqualNoQuantity(stack, interestedItemStack)) {
            InterfaceNetworkNode iface = getInterface(server);

            ItemStack wanted = iface.getExportFilterItems().getStackInSlot(slot);
            // This item is no longer being exported in the interface
            if (!API.instance().getComparer().isEqualNoQuantity(stack, wanted)) {
                return stack;
            }

            ItemStack got = iface.getExportItems().getStackInSlot(slot);

            int needed = wanted.getCount() - got.getCount();
            if (needed > stack.getCount()) {
                needed = stack.getCount();
            }

            if (needed > 0) {
                if (got.isEmpty()) {
                    iface.getExportItems().setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(stack, needed));
                } else {
                    iface.getExportItems().getStackInSlot(slot).grow(needed);
                    iface.markDirty();
                }

                stack.shrink(needed);
            }
        }

        return stack;
    }

    @Override
    public FluidStack intercept(MinecraftServer server, FluidStack stack) {
        return stack;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("Item", interestedItemStack.write(new CompoundNBT()));
        tag.putString("Dim", interfaceDimension.getRegistryName().toString());
        tag.putLong("Pos", interfacePos.toLong());
        tag.putInt("Slot", slot);

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

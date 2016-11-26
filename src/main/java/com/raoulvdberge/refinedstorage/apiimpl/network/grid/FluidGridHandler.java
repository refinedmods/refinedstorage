package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class FluidGridHandler implements IFluidGridHandler {
    private INetworkMaster network;

    public FluidGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onExtract(int hash, boolean shift, EntityPlayerMP player) {
        FluidStack stack = network.getFluidStorageCache().getList().get(hash);

        if (stack == null || stack.amount < Fluid.BUCKET_VOLUME) {
            return;
        }

        if (RSUtils.hasFluidBucket(stack)) {
            ItemStack bucket = network.extractItem(RSUtils.EMPTY_BUCKET, 1, false);

            if (bucket == null) {
                for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                    ItemStack slot = player.inventory.getStackInSlot(i);

                    if (API.instance().getComparer().isEqualNoQuantity(RSUtils.EMPTY_BUCKET, slot)) {
                        bucket = RSUtils.EMPTY_BUCKET.copy();

                        player.inventory.decrStackSize(i, 1);

                        break;
                    }
                }
            }

            if (bucket != null) {
                bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(network.extractFluid(stack, Fluid.BUCKET_VOLUME, false), true);

                if (shift) {
                    if (!player.inventory.addItemStackToInventory(bucket.copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), bucket);
                    }
                } else {
                    player.inventory.setItemStack(bucket);
                    player.updateHeldItem();
                }
            }
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(ItemStack container) {
        FluidStack stack = RSUtils.getFluidFromStack(container, true);

        if (stack != null && network.insertFluid(stack, stack.amount, true) == null) {
            FluidStack drained = RSUtils.getFluidFromStack(container, false);

            network.insertFluid(drained, drained.amount, false);
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        onInsert(player.inventory.getItemStack());

        player.updateHeldItem();
    }
}

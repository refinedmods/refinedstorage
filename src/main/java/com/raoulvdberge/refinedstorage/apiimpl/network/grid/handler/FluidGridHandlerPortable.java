package com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FluidGridHandlerPortable implements IFluidGridHandler {
    private IPortableGrid portableGrid;

    public FluidGridHandlerPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void onExtract(EntityPlayerMP player, int hash, boolean shift) {
        FluidStack stack = portableGrid.getFluidCache().getList().get(hash);

        if (stack == null || stack.amount < Fluid.BUCKET_VOLUME) {
            return;
        }

        if (StackUtils.hasFluidBucket(stack)) {
            ItemStack bucket = null;

            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack slot = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                    bucket = StackUtils.EMPTY_BUCKET.copy();

                    player.inventory.decrStackSize(i, 1);

                    break;
                }
            }

            if (bucket != null) {
                IFluidHandlerItem fluidHandler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                portableGrid.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(portableGrid.getFluidStorage().extract(stack, Fluid.BUCKET_VOLUME, IComparer.COMPARE_NBT, Action.PERFORM), true);

                if (shift) {
                    if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), fluidHandler.getContainer());
                    }
                } else {
                    player.inventory.setItemStack(fluidHandler.getContainer());
                    player.updateHeldItem();
                }

                portableGrid.drainEnergy(RS.INSTANCE.config.portableGridExtractUsage);
            }
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack container) {
        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (result.getValue() != null && portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().amount, Action.SIMULATE) == null) {
            portableGrid.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().amount, Action.PERFORM);

            portableGrid.drainEnergy(RS.INSTANCE.config.portableGridInsertUsage);

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(EntityPlayerMP player) {
        player.inventory.setItemStack(StackUtils.nullToEmpty(onInsert(player, player.inventory.getItemStack())));
        player.updateHeldItem();
    }

    @Override
    public ItemStack onShiftClick(EntityPlayerMP player, ItemStack container) {
        return StackUtils.nullToEmpty(onInsert(player, container));
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity, boolean noPreview) {
        // NO OP
    }

    @Override
    public void onCraftingRequested(EntityPlayerMP player, int hash, int quantity) {
        // NO OP
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FluidGridHandlerPortable implements IFluidGridHandler {
    private IPortableGrid portableGrid;

    public FluidGridHandlerPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void onExtract(ServerPlayerEntity player, int hash, boolean shift) {
        FluidStack stack = portableGrid.getFluidCache().getList().get(hash);

        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
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
                bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fluidHandler -> {
                    portableGrid.getFluidStorageTracker().changed(player, stack.copy());

                    fluidHandler.fill(portableGrid.getFluidStorage().extract(stack, FluidAttributes.BUCKET_VOLUME, IComparer.COMPARE_NBT, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE);

                    if (shift) {
                        if (!player.inventory.addItemStackToInventory(fluidHandler.getContainer().copy())) {
                            InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), fluidHandler.getContainer());
                        }
                    } else {
                        player.inventory.setItemStack(fluidHandler.getContainer());
                        player.updateHeldItem();
                    }

                    portableGrid.drainEnergy(RS.INSTANCE.config.portableGridExtractUsage);
                });
            }
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(ServerPlayerEntity player, ItemStack container) {
        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.SIMULATE) == null) {
            portableGrid.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.PERFORM);

            portableGrid.drainEnergy(RS.INSTANCE.config.portableGridInsertUsage);

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(ServerPlayerEntity player) {
        player.inventory.setItemStack(StackUtils.nullToEmpty(onInsert(player, player.inventory.getItemStack())));
        player.updateHeldItem();
    }

    @Override
    public ItemStack onShiftClick(ServerPlayerEntity player, ItemStack container) {
        return StackUtils.nullToEmpty(onInsert(player, container));
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayerEntity player, int hash, int quantity, boolean noPreview) {
        // NO OP
    }

    @Override
    public void onCraftingRequested(ServerPlayerEntity player, int hash, int quantity) {
        // NO OP
    }
}

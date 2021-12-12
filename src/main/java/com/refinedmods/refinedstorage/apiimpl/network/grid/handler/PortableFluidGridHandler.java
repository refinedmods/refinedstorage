package com.refinedmods.refinedstorage.apiimpl.network.grid.handler;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PortableFluidGridHandler implements IFluidGridHandler {
    private final IPortableGrid portableGrid;

    public PortableFluidGridHandler(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void onExtract(ServerPlayerEntity player, UUID id, boolean shift) {
        if (!portableGrid.isGridActive()) {
            return;
        }

        FluidStack stack = portableGrid.getFluidCache().getList().get(id);

        if (stack == null || stack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
            return;
        }

        ItemStack bucket = ItemStack.EMPTY;

        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack slot = player.inventory.getItem(i);

            if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                bucket = StackUtils.EMPTY_BUCKET.copy();

                player.inventory.removeItem(i, 1);

                break;
            }
        }

        if (!bucket.isEmpty()) {
            bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).ifPresent(fluidHandler -> {
                portableGrid.getFluidStorageTracker().changed(player, stack.copy());

                fluidHandler.fill(portableGrid.getFluidStorage().extract(stack, FluidAttributes.BUCKET_VOLUME, IComparer.COMPARE_NBT, Action.PERFORM), IFluidHandler.FluidAction.EXECUTE);

                if (shift) {
                    if (!player.inventory.add(fluidHandler.getContainer().copy())) {
                        InventoryHelper.dropItemStack(player.getCommandSenderWorld(), player.getX(), player.getY(), player.getZ(), fluidHandler.getContainer());
                    }
                } else {
                    player.inventory.setCarried(fluidHandler.getContainer());
                    player.broadcastCarriedItem();
                }

                portableGrid.drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getExtractUsage());
            });
        }
    }

    @Override
    @Nonnull
    public ItemStack onInsert(ServerPlayerEntity player, ItemStack container) {
        if (!portableGrid.isGridActive()) {
            return container;
        }

        Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

        if (!result.getValue().isEmpty() && portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.SIMULATE).isEmpty()) {
            portableGrid.getFluidStorageTracker().changed(player, result.getValue().copy());

            result = StackUtils.getFluid(container, false);

            portableGrid.getFluidStorage().insert(result.getValue(), result.getValue().getAmount(), Action.PERFORM);

            portableGrid.drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getInsertUsage());

            return result.getLeft();
        }

        return container;
    }

    @Override
    public void onInsertHeldContainer(ServerPlayerEntity player) {
        player.inventory.setCarried(onInsert(player, player.inventory.getCarried()));
        player.broadcastCarriedItem();
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview) {
        // NO OP
    }

    @Override
    public void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity) {
        // NO OP
    }
}

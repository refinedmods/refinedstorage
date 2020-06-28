package com.refinedmods.refinedstorage.apiimpl.network.node.exporter;

import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ExporterOutputInterceptor implements IOutputInterceptor {
    private final ItemStack interestedItemStack;
    private final FluidStack interestedFluidStack;
    private final ExporterNetworkNode exporter;

    public ExporterOutputInterceptor(ItemStack interestedItemStack, FluidStack interestedFluidStack, ExporterNetworkNode exporter) {
        this.interestedItemStack = interestedItemStack;
        this.interestedFluidStack = interestedFluidStack;
        this.exporter = exporter;
    }

    @Override
    public ItemStack intercept(ItemStack stack) {
        if (API.instance().getComparer().isEqualNoQuantity(stack, interestedItemStack)) {
            IItemHandler handler = exporter.getFacingItemHandler();
            if (handler == null) {
                return stack;
            }

            int toInsertIntoNetwork = 0;
            int toInsertIntoInventory = stack.getCount();

            if (exporter.getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                int needed = exporter.getStackInteractCountForRegulatorUpgrade(handler, interestedItemStack, toInsertIntoInventory);

                if (toInsertIntoInventory > needed) {
                    toInsertIntoNetwork = toInsertIntoInventory - needed;
                    toInsertIntoInventory = needed;
                }
            }

            if (toInsertIntoInventory > 0) {
                ItemStack remainder = ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, toInsertIntoInventory), false);

                toInsertIntoNetwork += remainder.getCount();
            }

            stack = ItemHandlerHelper.copyStackWithSize(stack, toInsertIntoNetwork);
        }

        return stack;
    }

    @Override
    public FluidStack intercept(FluidStack stack) {
        if (API.instance().getComparer().isEqual(stack, interestedFluidStack, IComparer.COMPARE_NBT)) {
            IFluidHandler handler = exporter.getFacingFluidHandler();
            if (handler == null) {
                return stack;
            }

            int toInsertIntoNetwork = 0;
            int toInsertIntoInventory = stack.getAmount();

            if (exporter.getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                int needed = exporter.getStackInteractCountForRegulatorUpgrade(handler, interestedFluidStack, toInsertIntoInventory);

                if (toInsertIntoInventory > needed) {
                    toInsertIntoNetwork = toInsertIntoInventory - needed;
                    toInsertIntoInventory = needed;
                }
            }

            if (toInsertIntoInventory > 0) {
                int filled = handler.fill(StackUtils.copy(stack, toInsertIntoInventory), IFluidHandler.FluidAction.EXECUTE);
                int remainder = toInsertIntoInventory - filled;

                toInsertIntoNetwork += remainder;
            }

            stack = StackUtils.copy(stack, toInsertIntoNetwork);
        }

        return stack;
    }
}

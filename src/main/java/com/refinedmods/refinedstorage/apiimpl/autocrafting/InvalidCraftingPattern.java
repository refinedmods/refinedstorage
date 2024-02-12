package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;
import java.util.List;

public class InvalidCraftingPattern implements ICraftingPattern {
    private static final String EXCEPTION_MESSAGE = "Crafting pattern is invalid";

    private final CraftingPatternContext context;
    private final Component errorMessage;

    public InvalidCraftingPattern(CraftingPatternContext context, Component errorMessage) {
        this.context = context;
        this.errorMessage = errorMessage;
    }

    @Override
    public ICraftingPatternContainer getContainer() {
        return context.getContainer();
    }

    @Override
    public ItemStack getStack() {
        return context.getStack();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Nullable
    @Override
    public Component getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isProcessing() {
        return false;
    }

    @Override
    public List<NonNullList<ItemStack>> getInputs() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public ItemStack getOutput(NonNullList<ItemStack> took, RegistryAccess registryAccess) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getByproducts() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public List<NonNullList<FluidStack>> getFluidInputs() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<FluidStack> getFluidOutputs() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public ResourceLocation getCraftingTaskFactoryId() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }
}

package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class InvalidCraftingPattern implements ICraftingPattern {
    private static final String EXCEPTION_MESSAGE = "Crafting pattern is invalid";

    private final CraftingPatternContext context;
    private final ITextComponent errorMessage;

    public InvalidCraftingPattern(CraftingPatternContext context, ITextComponent errorMessage) {
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
    public ITextComponent getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isProcessing() {
        return false;
    }

    @Override
    public List<NonNullList<ItemStack>> getInputs() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public ItemStack getOutput(NonNullList<ItemStack> took) {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getByproducts() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public List<NonNullList<FluidStack>> getFluidInputs() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public NonNullList<FluidStack> getFluidOutputs() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public ResourceLocation getCraftingTaskFactoryId() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }
}

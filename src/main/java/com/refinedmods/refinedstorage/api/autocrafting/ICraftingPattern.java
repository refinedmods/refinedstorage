package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a crafting pattern.
 */
public interface ICraftingPattern {
    /**
     * @return the {@link ICraftingPatternContainer} where the pattern is in
     */
    ICraftingPatternContainer getContainer();

    /**
     * @return the crafting pattern stack
     */
    ItemStack getStack();

    /**
     * @return true if the crafting pattern is valid, false otherwise
     */
    boolean isValid();

    /**
     * @return an error message when this pattern is not valid, or null if there's no message
     */
    @Nullable
    Component getErrorMessage();

    /**
     * @return true if the crafting pattern can be treated as a processing pattern, false otherwise
     */
    boolean isProcessing();

    /**
     * @return the inputs per slot
     */
    List<NonNullList<ItemStack>> getInputs();

    /**
     * @return the outputs
     */
    NonNullList<ItemStack> getOutputs();

    /**
     * @param took           the items took per slot
     * @param registryAccess the registry accessor
     * @return the output based on the items took
     */
    ItemStack getOutput(NonNullList<ItemStack> took, RegistryAccess registryAccess);

    /**
     * @return the byproducts
     */
    NonNullList<ItemStack> getByproducts();

    /**
     * @param took the items took per slot
     * @return the byproducts based on the items took
     */
    NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took);

    /**
     * @return the fluid inputs per slot
     */
    List<NonNullList<FluidStack>> getFluidInputs();

    /**
     * @return the fluid outputs
     */
    NonNullList<FluidStack> getFluidOutputs();

    /**
     * @return the id of the factory that creates a crafting task for this pattern, as defined in the {@link ICraftingTaskRegistry}
     */
    ResourceLocation getCraftingTaskFactoryId();
}

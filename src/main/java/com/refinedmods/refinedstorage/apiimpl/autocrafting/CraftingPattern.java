package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTaskFactory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingPattern implements ICraftingPattern {
    private final CraftingPatternContext context;
    private final boolean processing;
    private final boolean exact;
    @Nullable
    private final CraftingRecipe recipe;
    private final CraftingPatternInputs inputs;
    private final CraftingPatternOutputs outputs;
    @Nullable
    private final AllowedTagList allowedTagList;

    public CraftingPattern(CraftingPatternContext context, boolean processing, boolean exact, @Nullable CraftingRecipe recipe, CraftingPatternInputs inputs, CraftingPatternOutputs outputs, @Nullable AllowedTagList allowedTagList) {
        this.context = context;
        this.processing = processing;
        this.exact = exact;
        this.recipe = recipe;
        this.inputs = inputs;
        this.outputs = outputs;
        this.allowedTagList = allowedTagList;
    }

    @Nullable
    public AllowedTagList getAllowedTagList() {
        return allowedTagList;
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
        return true;
    }

    @Nullable
    @Override
    public Component getErrorMessage() {
        return null;
    }

    @Override
    public boolean isProcessing() {
        return processing;
    }

    @Override
    public List<NonNullList<ItemStack>> getInputs() {
        return inputs.getInputs();
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        return outputs.getOutputs();
    }

    public ItemStack getOutput(NonNullList<ItemStack> took, RegistryAccess registryAccess) {
        if (processing) {
            throw new IllegalStateException("Cannot get crafting output from processing pattern");
        }

        if (took.size() != inputs.getInputs().size()) {
            throw new IllegalArgumentException("The items that are taken (" + took.size() + ") should match the inputs for this pattern (" + inputs.getInputs().size() + ")");
        }

        CraftingContainer craftingContainer = new DummyCraftingContainer();

        for (int i = 0; i < took.size(); ++i) {
            craftingContainer.setItem(i, took.get(i));
        }

        ItemStack result = recipe.assemble(craftingContainer, registryAccess);
        if (result.isEmpty()) {
            throw new IllegalStateException("Cannot have empty result");
        }

        return result;
    }

    @Override
    public NonNullList<ItemStack> getByproducts() {
        if (processing) {
            throw new IllegalStateException("Cannot get byproduct outputs from processing pattern");
        }

        return outputs.getByproducts();
    }

    @Override
    public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
        if (processing) {
            throw new IllegalStateException("Cannot get byproduct outputs from processing pattern");
        }

        if (took.size() != inputs.getInputs().size()) {
            throw new IllegalArgumentException("The items that are taken (" + took.size() + ") should match the inputs for this pattern (" + inputs.getInputs().size() + ")");
        }

        CraftingContainer craftingContainer = new DummyCraftingContainer();

        for (int i = 0; i < took.size(); ++i) {
            craftingContainer.setItem(i, took.get(i));
        }

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(craftingContainer);
        NonNullList<ItemStack> sanitized = NonNullList.create();

        for (ItemStack item : remainingItems) {
            if (!item.isEmpty()) {
                sanitized.add(item);
            }
        }

        return sanitized;
    }

    @Override
    public List<NonNullList<FluidStack>> getFluidInputs() {
        return inputs.getFluidInputs();
    }

    @Override
    public NonNullList<FluidStack> getFluidOutputs() {
        return outputs.getFluidOutputs();
    }

    @Override
    public ResourceLocation getCraftingTaskFactoryId() {
        return CraftingTaskFactory.ID;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof ICraftingPattern)) {
            return false;
        }

        ICraftingPattern other = (ICraftingPattern) otherObj;

        if (other.isProcessing() != processing) {
            return false;
        }

        if ((other.getInputs().size() != inputs.getInputs().size()) ||
            (other.getFluidInputs().size() != inputs.getFluidInputs().size()) ||
            (other.getOutputs().size() != outputs.getOutputs().size()) ||
            (other.getFluidOutputs().size() != outputs.getFluidOutputs().size())) {
            return false;
        }

        if (!processing && other.getByproducts().size() != outputs.getByproducts().size()) {
            return false;
        }

        for (int i = 0; i < inputs.getInputs().size(); ++i) {
            List<ItemStack> inputsForSlot = inputs.getInputs().get(i);
            List<ItemStack> otherInputsForSlot = other.getInputs().get(i);

            if (inputsForSlot.size() != otherInputsForSlot.size()) {
                return false;
            }

            for (int j = 0; j < inputsForSlot.size(); ++j) {
                if (!API.instance().getComparer().isEqual(inputsForSlot.get(j), otherInputsForSlot.get(j))) {
                    return false;
                }
            }
        }

        for (int i = 0; i < inputs.getFluidInputs().size(); ++i) {
            List<FluidStack> inputsForSlot = inputs.getFluidInputs().get(i);
            List<FluidStack> otherInputsForSlot = other.getFluidInputs().get(i);

            if (inputsForSlot.size() != otherInputsForSlot.size()) {
                return false;
            }

            for (int j = 0; j < inputsForSlot.size(); ++j) {
                if (!API.instance().getComparer().isEqual(inputsForSlot.get(j), otherInputsForSlot.get(j), IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < outputs.getOutputs().size(); ++i) {
            if (!API.instance().getComparer().isEqual(outputs.getOutputs().get(i), other.getOutputs().get(i))) {
                return false;
            }
        }

        for (int i = 0; i < outputs.getFluidOutputs().size(); ++i) {
            if (!API.instance().getComparer().isEqual(outputs.getFluidOutputs().get(i), other.getFluidOutputs().get(i), IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
                return false;
            }
        }

        if (!processing) {
            for (int i = 0; i < outputs.getByproducts().size(); ++i) {
                if (!API.instance().getComparer().isEqual(outputs.getByproducts().get(i), other.getByproducts().get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + (processing ? 1 : 0);
        result = 31 * result + (exact ? 1 : 0);

        for (List<ItemStack> inputsForSlot : inputs.getInputs()) {
            for (ItemStack input : inputsForSlot) {
                result = 31 * result + API.instance().getItemStackHashCode(input);
            }
        }

        for (List<FluidStack> inputsForSlot : inputs.getFluidInputs()) {
            for (FluidStack input : inputsForSlot) {
                result = 31 * result + API.instance().getFluidStackHashCode(input);
            }
        }

        for (ItemStack output : outputs.getOutputs()) {
            result = 31 * result + API.instance().getItemStackHashCode(output);
        }

        for (FluidStack output : outputs.getFluidOutputs()) {
            result = 31 * result + API.instance().getFluidStackHashCode(output);
        }

        for (ItemStack byproduct : outputs.getByproducts()) {
            result = 31 * result + API.instance().getItemStackHashCode(byproduct);
        }

        return result;
    }

    public static class DummyCraftingContainer extends TransientCraftingContainer {
        public DummyCraftingContainer() {
            super(new AbstractContainerMenu(null, 0) {
                @Override
                public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                    return ItemStack.EMPTY;
                }

                @Override
                public boolean stillValid(Player player) {
                    return true;
                }
            }, 3, 3);
        }
    }
}

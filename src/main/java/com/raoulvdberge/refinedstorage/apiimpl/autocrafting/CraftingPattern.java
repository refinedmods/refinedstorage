package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingPattern implements ICraftingPattern {
    private final ICraftingPatternContainer container;
    private final ItemStack stack;
    private final boolean processing;
    private final boolean exact;
    private final boolean valid;
    @Nullable
    private final ITextComponent errorMessage;
    @Nullable
    private final ICraftingRecipe recipe;
    private final List<NonNullList<ItemStack>> inputs;
    private final NonNullList<ItemStack> outputs;
    private final NonNullList<ItemStack> byproducts;
    private final List<NonNullList<FluidStack>> fluidInputs;
    private final NonNullList<FluidStack> fluidOutputs;
    @Nullable
    private final AllowedTagList allowedTagList;

    public CraftingPattern(ICraftingPatternContainer container, ItemStack stack, boolean processing, boolean exact, @Nullable ITextComponent errorMessage, boolean valid, @Nullable ICraftingRecipe recipe, List<NonNullList<ItemStack>> inputs, NonNullList<ItemStack> outputs, NonNullList<ItemStack> byproducts, List<NonNullList<FluidStack>> fluidInputs, NonNullList<FluidStack> fluidOutputs, @Nullable AllowedTagList allowedTagList) {
        this.container = container;
        this.stack = stack;
        this.processing = processing;
        this.exact = exact;
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.recipe = recipe;
        this.inputs = inputs;
        this.outputs = outputs;
        this.byproducts = byproducts;
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
        this.allowedTagList = allowedTagList;
    }

    @Nullable
    public AllowedTagList getAllowedTagList() {
        return allowedTagList;
    }

    @Override
    public ICraftingPatternContainer getContainer() {
        return container;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Nullable
    @Override
    public ITextComponent getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isProcessing() {
        return processing;
    }

    @Override
    public List<NonNullList<ItemStack>> getInputs() {
        return inputs;
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    public ItemStack getOutput(NonNullList<ItemStack> took) {
        if (processing) {
            throw new IllegalStateException("Cannot get crafting output from processing pattern");
        }

        if (took.size() != inputs.size()) {
            throw new IllegalArgumentException("The items that are taken (" + took.size() + ") should match the inputs for this pattern (" + inputs.size() + ")");
        }

        CraftingInventory inv = new DummyCraftingInventory();

        for (int i = 0; i < took.size(); ++i) {
            inv.setInventorySlotContents(i, took.get(i));
        }

        ItemStack result = recipe.getCraftingResult(inv);
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

        return byproducts;
    }

    @Override
    public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
        if (processing) {
            throw new IllegalStateException("Cannot get byproduct outputs from processing pattern");
        }

        if (took.size() != inputs.size()) {
            throw new IllegalArgumentException("The items that are taken (" + took.size() + ") should match the inputs for this pattern (" + inputs.size() + ")");
        }

        CraftingInventory inv = new DummyCraftingInventory();

        for (int i = 0; i < took.size(); ++i) {
            inv.setInventorySlotContents(i, took.get(i));
        }

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(inv);
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
        return fluidInputs;
    }

    @Override
    public NonNullList<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    public ResourceLocation getId() {
        return CraftingTaskFactory.ID;
    }

    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + (processing ? 1 : 0);
        result = 31 * result + (exact ? 1 : 0);

        for (List<ItemStack> inputs : this.inputs) {
            for (ItemStack input : inputs) {
                result = 31 * result + API.instance().getItemStackHashCode(input);
            }
        }

        for (List<FluidStack> inputs : this.fluidInputs) {
            for (FluidStack input : inputs) {
                result = 31 * result + API.instance().getFluidStackHashCode(input);
            }
        }

        for (ItemStack output : this.outputs) {
            result = 31 * result + API.instance().getItemStackHashCode(output);
        }

        for (FluidStack output : this.fluidOutputs) {
            result = 31 * result + API.instance().getFluidStackHashCode(output);
        }

        for (ItemStack byproduct : this.byproducts) {
            result = 31 * result + API.instance().getItemStackHashCode(byproduct);
        }
        return result;
    }

    public static class DummyCraftingInventory extends CraftingInventory {
        public DummyCraftingInventory() {
            super(new Container(null, 0) {
                @Override
                public boolean canInteractWith(PlayerEntity player) {
                    return true;
                }
            }, 3, 3);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ICraftingPattern)) {
            return false;
        }
        ICraftingPattern pattern = (ICraftingPattern) o;
        if (pattern.isProcessing() == processing) {
            if (outputs.size() > 0 && outputs.size() == pattern.getOutputs().size()) {
                for (int i = 0; i < outputs.size(); i++) {
                    if (!API.instance().getComparer().isEqual(pattern.getOutputs().get(i), outputs.get(i))) {
                        return false;
                    }
                }
            }
            if (fluidOutputs.size() > 0 && fluidOutputs.size() == pattern.getFluidOutputs().size()) {
                for (int i = 0; i < fluidOutputs.size(); i++) {
                    if (!API.instance().getComparer().isEqual(pattern.getFluidOutputs().get(i), fluidOutputs.get(i), IComparer.COMPARE_NBT)) {
                        return false;
                    }
                }
            }
            if (fluidInputs.size() > 0 && fluidInputs.size() == pattern.getFluidInputs().size()) {
                for (int i = 0; i < fluidInputs.size(); i++) {
                    if (fluidInputs.get(i).size() > 0 && fluidInputs.get(i).size() == pattern.getFluidInputs().get(i).size()) {
                        for (int j= 0; j < fluidInputs.get(i).size(); j++) {
                            if (!API.instance().getComparer().isEqual(pattern.getFluidInputs().get(i).get(j), fluidInputs.get(i).get(j), IComparer.COMPARE_NBT)) {
                                return false;
                            }
                        }
                    }
                }

            }
            if (byproducts.size() > 0 && byproducts.size() == pattern.getByproducts().size()) {
                for (int i = 0; i < byproducts.size(); i++) {
                    if (!API.instance().getComparer().isEqual(pattern.getByproducts().get(i), byproducts.get(i))) {
                        return false;
                    }
                }
            }
            if (inputs.size() > 0 && inputs.size() == pattern.getInputs().size()) {
                for (int i = 0; i < inputs.size(); i++) {
                    if (inputs.get(i).size() == pattern.getInputs().get(i).size()) {
                        for (int j = 0; j < inputs.get(i).size(); j++) {
                            if (!API.instance().getComparer().isEqual(pattern.getInputs().get(i).get(j), inputs.get(i).get(j))) {
                                return false;
                            }
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}

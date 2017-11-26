package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import com.raoulvdberge.refinedstorage.apiimpl.util.Comparer;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingPattern implements ICraftingPattern {
    private ICraftingPatternContainer container;
    private ItemStack stack;
    private IRecipe recipe;
    private List<ItemStack> inputs = new ArrayList<>();
    private List<List<ItemStack>> oreInputs = new ArrayList<>();
    private List<ItemStack> outputs = new ArrayList<>();
    private List<ItemStack> byproducts = new ArrayList<>();
    private Integer hashCodeCached = null;

    public CraftingPattern(World world, ICraftingPatternContainer container, ItemStack stack) {
        this.container = container;
        this.stack = Comparer.stripTags(stack);

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            ItemStack slot = ItemPattern.getSlot(stack, i);

            inputs.add(Comparer.stripTags(slot));

            if (slot != null) {
                inv.setInventorySlotContents(i, slot);
            }
        }

        if (!ItemPattern.isProcessing(stack)) {
            for (IRecipe r : CraftingManager.REGISTRY) {
                if (r.matches(inv, world)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe != null) {
                ItemStack output = recipe.getCraftingResult(inv);

                if (!output.isEmpty()) {
                    outputs.add(Comparer.stripTags(output.copy()));

                    if (isOredict()) {
                        List<List<ItemStack>> inputs = new LinkedList<>();

                        for (Ingredient ingredient : recipe.getIngredients()) {
                            inputs.add(Arrays.asList(ingredient.getMatchingStacks()));
                        }

                        for (List<ItemStack> input : inputs) {
                            if (input.isEmpty()) {
                                oreInputs.add(Collections.emptyList());
                            } else {
                                List<ItemStack> cleaned = new LinkedList<>();
                                for (ItemStack in : input) {
                                    cleaned.add(Comparer.stripTags(in.copy()));
                                }
                                oreInputs.add(cleaned);
                            }
                        }
                    }

                    for (ItemStack remaining : recipe.getRemainingItems(inv)) {
                        if (remaining != null) {
                            ItemStack cleaned = Comparer.stripTags(remaining.copy());
                            byproducts.add(cleaned);
                        }
                    }
                }
            }
        } else {
            outputs = ItemPattern.getOutputs(stack).stream().collect(Collectors.toList());
        }

        if (oreInputs.isEmpty()) {
            for (ItemStack input : inputs) {
                if (input == null) {
                    oreInputs.add(Collections.emptyList());
                } else if (!input.isEmpty()) {
                    int[] ids = OreDictionary.getOreIDs(input);
                    if (ids == null || ids.length == 0) {
                        oreInputs.add(Collections.singletonList(Comparer.stripTags(input)));
                    } else if (isOredict()) {
                        List<ItemStack> oredict = Arrays.stream(ids)
                            .mapToObj(OreDictionary::getOreName)
                            .map(OreDictionary::getOres)
                            .flatMap(List::stream)
                            .map(ItemStack::copy)
                            .map(Comparer::stripTags)
                            .map(s -> {
                                s.setCount(input.getCount());
                                return s;
                            })
                            .collect(Collectors.toList());
                        // Add original stack as first, should prevent some issues
                        oredict.add(0, Comparer.stripTags(input.copy()));
                        oreInputs.add(oredict);
                    } else {
                        oreInputs.add(Collections.singletonList(Comparer.stripTags(input)));
                    }
                }
            }
        }
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
        return !inputs.isEmpty() && inputs.stream().filter(Objects::nonNull).count() > 0 && !outputs.isEmpty();
    }

    @Override
    public boolean isProcessing() {
        return ItemPattern.isProcessing(stack);
    }

    @Override
    public boolean isOredict() {
        return ItemPattern.isOredict(stack);
    }

    @Override
    public boolean isBlocking() {
        return ItemPattern.isBlocking(stack);
    }

    @Override
    public List<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public List<List<ItemStack>> getOreInputs() {
        return oreInputs;
    }

    @Override
    @Nullable
    public List<ItemStack> getOutputs(ItemStack[] took) {
        List<ItemStack> outputs = new ArrayList<>();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            if (took[i] != null) {
                inv.setInventorySlotContents(i, took[i]);
            }
        }

        ItemStack cleaned = recipe.getCraftingResult(inv);
        if (cleaned.isEmpty()) {
            return null;
        }
        outputs.add(cleaned.copy());

        return outputs;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Override
    public List<ItemStack> getByproducts(ItemStack[] took) {
        List<ItemStack> byproducts = new ArrayList<>();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            if (took[i] != null) {
                inv.setInventorySlotContents(i, took[i]);
            }
        }

        for (ItemStack remaining : recipe.getRemainingItems(inv)) {
            if (remaining != null) {
                byproducts.add(remaining.copy());
            }
        }

        return byproducts;
    }

    @Override
    public List<ItemStack> getByproducts() {
        return byproducts;
    }

    @Override
    public String getId() {
        return CraftingTaskFactory.ID;
    }

    @Override
    public int getQuantityPerRequest(ItemStack requested, int compare) {
        int quantity = 0;
        requested = Comparer.stripTags(requested.copy());
        for (ItemStack output : outputs) {
            if (API.instance().getComparer().isEqual(requested, output, compare)) {
                quantity += output.getCount();

                if (!ItemPattern.isProcessing(stack)) {
                    break;
                }
            }
        }

        return quantity;
    }

    @Override
    public ItemStack getActualOutput(ItemStack requested, int compare) {
        requested = Comparer.stripTags(requested.copy());
        for (ItemStack output : outputs) {
            if (API.instance().getComparer().isEqual(requested, output, compare)) {
                return output.copy();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CraftingPattern{" +
            "container=" + container +
            ", inputs=" + inputs +
            ", outputs=" + outputs +
            ", byproducts=" + byproducts +
            '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof ICraftingPattern && this.alike((ICraftingPattern) obj));
    }

    @Override
    public int hashCode() {
        if (hashCodeCached == null) {
            hashCodeCached = 0;
            for (ItemStack outputItemStack : this.getOutputs()) {
                int itemHashCode = 0;
                itemHashCode = outputItemStack.getCount();
                itemHashCode = itemHashCode * 31 + outputItemStack.getItem().hashCode();
                itemHashCode = itemHashCode * 31 + outputItemStack.getItemDamage();
                itemHashCode = itemHashCode * 31 + Objects.hashCode(outputItemStack.getTagCompound());
                hashCodeCached = hashCodeCached * 31 + itemHashCode;
            }
        }
        return hashCodeCached;
    }

    @Override
    public boolean alike(ICraftingPattern other) {
        if (other == this) {
            return true;
        }

        if (other.getId().equals(this.getId())
            && other.isOredict() == this.isOredict()
            && other.isBlocking() == this.isBlocking()
            && other.isProcessing() == this.isProcessing()
            && other.getOreInputs().size() == this.getOreInputs().size()
            && other.getOutputs().size() == this.getOutputs().size()) {
            boolean same = true;
            for (int i = 0; i < other.getOreInputs().size(); i++) {
                same &= other.getOreInputs().get(i).size() == this.getOreInputs().get(i).size();
            }
            int j = 0;
            while (same && j < other.getOutputs().size()) {
                same = ItemStack.areItemStacksEqual(other.getOutputs().get(j), this.getOutputs().get(j));
                j++;
            }
            int i = 0;
            while (same && i < other.getOreInputs().size()) {
                List<ItemStack> otherList = other.getOreInputs().get(i);
                List<ItemStack> thisList = this.getOreInputs().get(i);
                j = 0;
                while (same && j < otherList.size()) {
                    same = ItemStack.areItemStacksEqual(otherList.get(j), thisList.get(j));
                    j++;
                }
                i++;
            }
            return same;
        }
        return false;
    }
}

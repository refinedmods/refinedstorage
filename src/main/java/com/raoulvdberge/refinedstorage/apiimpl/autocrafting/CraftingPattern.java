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
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftingPattern implements ICraftingPattern {
    private ICraftingPatternContainer container;
    private ItemStack stack;
    private IRecipe recipe;
    private List<ItemStack> inputs = new ArrayList<>();
    private List<List<ItemStack>> oreInputs = new ArrayList<>();
    private List<ItemStack> outputs = new ArrayList<>();
    private List<ItemStack> byproducts = new ArrayList<>();

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
            recipe = CraftingManager.getInstance().getRecipeList().stream().filter(r -> r.matches(inv, world)).findFirst().orElse(null);
            if (recipe != null) {
                ItemStack output = recipe.getCraftingResult(inv);

                if (!output.isEmpty()) {
                    boolean shapedOre = recipe instanceof ShapedOreRecipe;
                    // It is a dirty fix, but hey someone has to do it. ~ way2muchnoise 2016 "bite me"
                    boolean mekanism = recipe.getClass().getName().equals("mekanism.common.recipe.ShapedMekanismRecipe");

                    outputs.add(Comparer.stripTags(output.copy()));

                    if ((isOredict() && shapedOre) || mekanism) {
                        Object[] inputs = new Object[0];
                        if (shapedOre) {
                            inputs = ((ShapedOreRecipe) recipe).getInput();
                        } else {
                            try {
                                inputs = (Object[]) recipe.getClass().getMethod("getInput").invoke(recipe);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                        for (Object input : inputs) {
                            if (input == null) {
                                oreInputs.add(Collections.emptyList());
                            } else if (input instanceof ItemStack) {
                                oreInputs.add(Collections.singletonList(Comparer.stripTags(((ItemStack) input).copy())));
                            } else {
                                List<ItemStack> cleaned = new LinkedList<>();
                                for (ItemStack in : (List<ItemStack>) input) {
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
            outputs = ItemPattern.getOutputs(stack).stream().map(Comparer::stripTags).collect(Collectors.toList());
        }

        if (oreInputs.isEmpty()) {
            for (ItemStack input : inputs) {
                if (input == null) {
                    oreInputs.add(Collections.emptyList());
                } else {
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
        return inputs.stream().filter(Objects::nonNull).count() > 0 && !outputs.isEmpty();
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
        outputs.add(Comparer.stripTags(cleaned.copy()));

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
}

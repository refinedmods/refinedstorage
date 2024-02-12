package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.google.common.primitives.Ints;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.SerializationUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import javax.annotation.Nullable;
import java.util.*;

public class NodeRequirements {
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_FLUIDS_TO_USE = "FluidsToUse";

    private static final String NBT_ITEMS_NEEDED_PER_CRAFT = "ItemsNeededPerCraft";
    private static final String NBT_FLUIDS_NEEDED_PER_CRAFT = "FluidsNeededPerCraft";

    private final Map<Integer, IStackList<ItemStack>> itemRequirements = new LinkedHashMap<>();
    private final Map<Integer, Integer> itemsNeededPerCraft = new LinkedHashMap<>();

    private final Map<Integer, IStackList<FluidStack>> fluidRequirements = new LinkedHashMap<>();
    private final Map<Integer, Integer> fluidsNeededPerCraft = new LinkedHashMap<>();

    @Nullable
    private List<ItemStack> cachedSimulatedItemRequirementSet = null;
    @Nullable
    private List<FluidStack> cachedSimulatedFluidRequirementSet = null;

    public void addItemRequirement(int ingredientNumber, ItemStack stack, int size, int perCraft) {
        if (!itemsNeededPerCraft.containsKey(ingredientNumber)) {
            itemsNeededPerCraft.put(ingredientNumber, perCraft);
        }

        IStackList<ItemStack> list = itemRequirements.computeIfAbsent(ingredientNumber, key -> API.instance().createItemStackList());
        list.add(stack, size);
        cachedSimulatedItemRequirementSet = null;
    }

    public void addFluidRequirement(int ingredientNumber, FluidStack stack, int size, int perCraft) {
        if (!fluidsNeededPerCraft.containsKey(ingredientNumber)) {
            fluidsNeededPerCraft.put(ingredientNumber, perCraft);
        }

        IStackList<FluidStack> list = fluidRequirements.computeIfAbsent(ingredientNumber, key -> API.instance().createFluidStackList());
        list.add(stack, size);
        cachedSimulatedFluidRequirementSet = null;
    }

    public List<ItemStack> getSingleItemRequirementSet(boolean simulate) {
        List<ItemStack> cached = cachedSimulatedItemRequirementSet;
        if (simulate && cached != null) {
            return cached;
        }

        List<ItemStack> toReturn = new ArrayList<>();

        for (int i = 0; i < itemRequirements.size(); i++) {
            int needed = itemsNeededPerCraft.get(i);

            if (!itemRequirements.get(i).isEmpty()) {
                Iterator<StackListEntry<ItemStack>> it = itemRequirements.get(i).getStacks().iterator();

                while (needed > 0 && it.hasNext()) {
                    ItemStack toUse = it.next().getStack();

                    if (needed < toUse.getCount()) {
                        if (!simulate) {
                            itemRequirements.get(i).remove(toUse, needed);
                        }

                        toReturn.add(ItemHandlerHelper.copyStackWithSize(toUse, needed));

                        needed = 0;
                    } else {
                        if (!simulate) {
                            it.remove();
                        }

                        toReturn.add(toUse);

                        needed -= toUse.getCount();
                    }
                }
            } else {
                return null;
            }
        }

        cachedSimulatedItemRequirementSet = simulate ? toReturn : null;

        return toReturn;
    }

    public List<FluidStack> getSingleFluidRequirementSet(boolean simulate) {
        List<FluidStack> cached = cachedSimulatedFluidRequirementSet;
        if (simulate && cached != null) {
            return cached;
        }

        List<FluidStack> toReturn = new ArrayList<>();

        for (int i = 0; i < fluidRequirements.size(); i++) {
            int needed = fluidsNeededPerCraft.get(i);

            if (!fluidRequirements.get(i).isEmpty()) {
                Iterator<StackListEntry<FluidStack>> it = fluidRequirements.get(i).getStacks().iterator();

                while (needed > 0 && it.hasNext()) {
                    FluidStack toUse = it.next().getStack();

                    if (needed < toUse.getAmount()) {
                        if (!simulate) {
                            fluidRequirements.get(i).remove(toUse, needed);
                        }

                        FluidStack stack = toUse.copy();
                        stack.setAmount(needed);
                        toReturn.add(stack);

                        needed = 0;
                    } else {
                        if (!simulate) {
                            it.remove();
                        }

                        toReturn.add(toUse);

                        needed -= toUse.getAmount();
                    }
                }
            } else {
                return null;
            }
        }

        cachedSimulatedFluidRequirementSet = simulate ? toReturn : null;

        return toReturn;
    }

    public void readFromNbt(CompoundTag tag) throws CraftingTaskReadException {
        ListTag itemRequirementsTag = tag.getList(NBT_ITEMS_TO_USE, Tag.TAG_LIST);
        for (int i = 0; i < itemRequirementsTag.size(); i++) {
            itemRequirements.put(i, SerializationUtil.readItemStackList(itemRequirementsTag.getList(i)));
        }

        List<Integer> itemsNeededPerCraftTag = Ints.asList(tag.getIntArray(NBT_ITEMS_NEEDED_PER_CRAFT));
        for (int i = 0; i < itemsNeededPerCraftTag.size(); i++) {
            itemsNeededPerCraft.put(i, itemsNeededPerCraftTag.get(i));
        }

        ListTag fluidRequirementsTag = tag.getList(NBT_FLUIDS_TO_USE, Tag.TAG_LIST);
        for (int i = 0; i < fluidRequirementsTag.size(); i++) {
            fluidRequirements.put(i, SerializationUtil.readFluidStackList(fluidRequirementsTag.getList(i)));
        }

        List<Integer> fluidsNeededPerCraftTag = Ints.asList(tag.getIntArray(NBT_FLUIDS_NEEDED_PER_CRAFT));
        for (int i = 0; i < fluidsNeededPerCraftTag.size(); i++) {
            fluidsNeededPerCraft.put(i, fluidsNeededPerCraftTag.get(i));
        }
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        ListTag itemRequirementsTag = new ListTag();
        for (IStackList<ItemStack> list : itemRequirements.values()) {
            itemRequirementsTag.add(SerializationUtil.writeItemStackList(list));
        }
        tag.put(NBT_ITEMS_TO_USE, itemRequirementsTag);

        tag.putIntArray(NBT_ITEMS_NEEDED_PER_CRAFT, Ints.toArray(itemsNeededPerCraft.values()));

        ListTag fluidRequirementsTag = new ListTag();
        for (IStackList<FluidStack> list : fluidRequirements.values()) {
            fluidRequirementsTag.add(SerializationUtil.writeFluidStackList(list));
        }
        tag.put(NBT_FLUIDS_TO_USE, fluidRequirementsTag);

        tag.putIntArray(NBT_FLUIDS_NEEDED_PER_CRAFT, Ints.toArray(fluidsNeededPerCraft.values()));

        return tag;
    }
}

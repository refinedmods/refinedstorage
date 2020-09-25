package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.google.common.primitives.Ints;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.SerializationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodeRequirements {
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_FLUIDS_TO_USE = "FluidsToUse";

    private static final String NBT_ITEMS_NEEDED_PER_CRAFT = "ItemsNeededPerCraft";
    private static final String NBT_FLUIDS_NEEDED_PER_CRAFT = "FluidsNeededPerCraft";

    private final Map<Integer, IStackList<ItemStack>> itemRequirements = new LinkedHashMap<>();
    private final Map<Integer, Integer> itemsNeededPerCraft = new LinkedHashMap<>();

    private final Map<Integer, IStackList<FluidStack>> fluidRequirements = new LinkedHashMap<>();
    private final Map<Integer, Integer> fluidsNeededPerCraft = new LinkedHashMap<>();

    public void addItemRequirement(int ingredientNumber, ItemStack stack, int size, int perCraft) {
        if (!itemsNeededPerCraft.containsKey(ingredientNumber)) {
            itemsNeededPerCraft.put(ingredientNumber, perCraft);
        }

        IStackList<ItemStack> list = itemRequirements.get(ingredientNumber);
        if (list == null) {
            itemRequirements.put(ingredientNumber, list = API.instance().createItemStackList());
        }

        list.add(stack, size);
    }

    public void addFluidRequirement(int ingredientNumber, FluidStack stack, int size, int perCraft) {
        if (!fluidsNeededPerCraft.containsKey(ingredientNumber)) {
            fluidsNeededPerCraft.put(ingredientNumber, perCraft);
        }

        IStackList<FluidStack> list = fluidRequirements.get(ingredientNumber);
        if (list == null) {
            fluidRequirements.put(ingredientNumber, list = API.instance().createFluidStackList());
        }

        list.add(stack, size);
    }

    public IStackList<ItemStack> getSingleItemRequirementSet(boolean simulate) {
        IStackList<ItemStack> toReturn = API.instance().createItemStackList();

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

                        toReturn.add(toUse, needed);

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
                throw new IllegalStateException("Bad!");
            }
        }

        return toReturn;
    }

    public IStackList<FluidStack> getSingleFluidRequirementSet(boolean simulate) {
        IStackList<FluidStack> toReturn = API.instance().createFluidStackList();

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

                        toReturn.add(toUse, needed);

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
                throw new IllegalStateException("Bad!");
            }
        }

        return toReturn;
    }

    public void readFromNbt(CompoundNBT tag) throws CraftingTaskReadException {
        ListNBT itemRequirements = tag.getList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_LIST);
        for (int i = 0; i < itemRequirements.size(); i++) {
            this.itemRequirements.put(i, SerializationUtil.readItemStackList(itemRequirements.getList(i)));
        }

        List<Integer> itemsNeededPerCraft = Ints.asList(tag.getIntArray(NBT_ITEMS_NEEDED_PER_CRAFT));
        for (int i = 0; i < itemsNeededPerCraft.size(); i++) {
            this.itemsNeededPerCraft.put(i, itemsNeededPerCraft.get(i));
        }

        ListNBT fluidRequirements = tag.getList(NBT_FLUIDS_TO_USE, Constants.NBT.TAG_LIST);
        for (int i = 0; i < fluidRequirements.size(); i++) {
            this.fluidRequirements.put(i, SerializationUtil.readFluidStackList(fluidRequirements.getList(i)));
        }

        List<Integer> fluidsNeededPerCraft = Ints.asList(tag.getIntArray(NBT_FLUIDS_NEEDED_PER_CRAFT));
        for (int i = 0; i < fluidsNeededPerCraft.size(); i++) {
            this.fluidsNeededPerCraft.put(i, fluidsNeededPerCraft.get(i));
        }
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        ListNBT itemRequirements = new ListNBT();
        for (IStackList<ItemStack> list : this.itemRequirements.values()) {
            itemRequirements.add(SerializationUtil.writeItemStackList(list));
        }
        tag.put(NBT_ITEMS_TO_USE, itemRequirements);

        tag.putIntArray(NBT_ITEMS_NEEDED_PER_CRAFT, Ints.toArray(itemsNeededPerCraft.values()));

        ListNBT fluidRequirements = new ListNBT();
        for (IStackList<FluidStack> list : this.fluidRequirements.values()) {
            fluidRequirements.add(SerializationUtil.writeFluidStackList(list));
        }
        tag.put(NBT_FLUIDS_TO_USE, fluidRequirements);

        tag.putIntArray(NBT_FLUIDS_NEEDED_PER_CRAFT, Ints.toArray(fluidsNeededPerCraft.values()));

        return tag;
    }
}

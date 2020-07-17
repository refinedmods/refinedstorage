package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.google.common.primitives.Ints;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;

import java.util.*;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_IS_PROCESSING = "IsProcessing";
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_NEEDED_PER_CRAFT = "NeededPerCraft";
    private static final String NBT_INGREDIENT_STORAGE = "IngredientStorage";
    private static final String NBT_AMOUNTS_PER_SLOT = "AmountsPerSlot";
    private static final String NBT_MAX_SLOTS = "MaxSlots";


    private final boolean root;
    protected int quantity;
    private ICraftingPattern pattern;
    private int maxSlots = 0;
    private Map<Integer, Ingredient> itemsToUse;

    Craft(ICraftingPattern pattern, boolean root) {
        this.pattern = pattern;
        this.root = root;
    }

    Craft(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.quantity = tag.getInt(NBT_QUANTITY);
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.root = tag.getBoolean(NBT_ROOT);
        ListNBT list = tag.getList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_COMPOUND);
        itemsToUse = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).equals(new CompoundNBT())) { //TODO remove compatibility in a future version
                this.quantity = 0;
                return;
            }
            this.itemsToUse.put(i, new Ingredient(list.getCompound(i)));
        }
        maxSlots = tag.getInt(NBT_MAX_SLOTS);
    }

    static Craft createCraftFromNBT(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_IS_PROCESSING) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    boolean hasItemsToUse() {
        return itemsToUse != null;
    }

    void initItemsToUse(Map<Integer, Ingredient> itemsToUse) {
        this.itemsToUse = itemsToUse;
        for (Ingredient in : itemsToUse.values()) {
            maxSlots = Math.max(maxSlots, in.calculatePerCraft());
        }
    }

    ICraftingPattern getPattern() {
        return pattern;
    }

    int getQuantity() {
        return quantity;
    }

    void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    void next() {
        quantity--;
    }

    boolean isRoot() {
        return root;
    }

    boolean hasItems() {
        return !itemsToUse.isEmpty();
    }

    void finishCalculation() {
        //NOOP
    }

    Map<Integer, IStackList<ItemStack>> getItemsToUse(boolean simulate) {
        Map<Integer, IStackList<ItemStack>> map = new HashMap<>();
        itemsToUse.forEach((x, y) -> map.put(x, y.getIngredients(simulate)));
        return map;
    }

    NonNullList<ItemStack> getItemsAsRecipe(Map<Integer, Queue<ItemStack>> extracted) {
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(getItemsAsList(extracted));
        return list;
    }

    List<ItemStack> getItemsAsList(Map<Integer, Queue<ItemStack>> extracted) {
        List<ItemStack> toReturn = new ArrayList<>(Collections.nCopies(this instanceof Crafting ? 9 : maxSlots + 1, ItemStack.EMPTY));
        extracted.forEach((i, queue) -> itemsToUse.get(i).amountPerSlot.forEach((slot, count) -> {
            int needed = count;
            boolean first = true;
            while (needed > 0) {
                if (queue.isEmpty()) {
                    throw new IllegalStateException("Recipe requires more items than extracted");
                }
                ItemStack queueStack = queue.peek();
                ItemStack stack = queueStack.copy();
                if (stack.getCount() > needed) {
                    stack.setCount(needed);
                    queueStack.setCount(queueStack.getCount() - needed);
                    needed = 0;
                } else {
                    needed -= stack.getCount();
                    queue.poll();
                }
                if (first) {
                    toReturn.set(slot, stack);
                    first = false;
                } else {
                    // need to make sure it doesn't break when multiple types of items are used for the same slot.
                    toReturn.add(slot, stack);
                }

            }
        }));
        return toReturn;
    }

    void addItemsToUse(int ingredientNumber, ItemStack stack, int size) {
        itemsToUse.get(ingredientNumber).storage.add(stack, size);
    }

    CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(NBT_QUANTITY, quantity);
        tag.putBoolean(NBT_IS_PROCESSING, this instanceof Processing);
        tag.putBoolean(NBT_ROOT, root);
        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        ListNBT list = new ListNBT();
        for (Ingredient ingredient : itemsToUse.values()) {
            list.add(ingredient.writeToNbt());
        }
        tag.put(NBT_ITEMS_TO_USE, list);
        tag.putInt(NBT_MAX_SLOTS, maxSlots);


        return tag;
    }

    static class Ingredient {
        IStackList<ItemStack> storage = API.instance().createItemStackList();
        int perCraft;
        public Map<Integer, Integer> amountPerSlot = new HashMap<>();

        public int calculatePerCraft() {
            int maxSlot = 0;
            for (Map.Entry<Integer, Integer> entry : amountPerSlot.entrySet()) {
                perCraft += entry.getValue();
                maxSlot = Math.max(maxSlot, entry.getKey());
            }
            return maxSlot;
        }

        Ingredient(){}

        //Extracts perCraft items from Storage
        IStackList<ItemStack> getIngredients(boolean simulate) {
            if (storage.isEmpty()) {
                LogManager.getLogger(Craft.class).warn("Craft requested more Items than available");
                return API.instance().createItemStackList();
            }
            int amount = perCraft;
            IStackList<ItemStack> list = API.instance().createItemStackList();
            Map<StackListEntry<ItemStack>, Integer> toRemove = new HashMap<>();
            for (StackListEntry<ItemStack> stack : storage.getStacks()) {
                int needed = Math.min(stack.getStack().getCount(), amount);
                if (!simulate) {
                    toRemove.put(stack, needed);
                }
                list.add(stack.getStack(), needed);
                amount -= needed;
                if (amount <= 0) break;
            }
            if (!simulate) {
                toRemove.forEach((x, y) -> storage.remove(x.getStack(), y));
            }
            return list;
        }

        public INBT writeToNbt() {
            CompoundNBT tag = new CompoundNBT();
            tag.put(NBT_INGREDIENT_STORAGE, CraftingTask.writeItemStackList(storage));
            tag.putInt(NBT_NEEDED_PER_CRAFT, perCraft);
            List<Integer> list = new ArrayList<>();
            amountPerSlot.forEach((x, y) -> {
                list.add(x);
                list.add(y);
            });
            tag.putIntArray(NBT_AMOUNTS_PER_SLOT, list);
            return tag;
        }

        public Ingredient(CompoundNBT nbt) throws CraftingTaskReadException {
            storage = CraftingTask.readItemStackList(nbt.getList(NBT_INGREDIENT_STORAGE, Constants.NBT.TAG_COMPOUND));
            perCraft = nbt.getInt(NBT_NEEDED_PER_CRAFT);
            List<Integer> ints = Ints.asList(nbt.getIntArray(NBT_AMOUNTS_PER_SLOT));
            for (int i = 0; i < ints.size(); i += 2) {
                amountPerSlot.put(ints.get(i), ints.get(i + 1));
            }
        }
    }
}

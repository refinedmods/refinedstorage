package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;

class Crafting extends Craft{

    private static final String NBT_TOOK = "Took";
    private static final String NBT_ITEMS_TO_PUT = "ToExtract";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_CRAFTED = "Crafted";


    private NonNullList<ItemStack> took;
    private IStackList<ItemStack> toExtractTotal;

    private Map<IStackList<ItemStack>, Integer> toExtract = new LinkedHashMap<>();
    private Map<NonNullList<ItemStack>, Integer> counts = null;
    private int crafted = 0;

    public Crafting(ICraftingPattern pattern, boolean root ) {
        this.pattern = pattern;
        this.root = root;
        this.containers.add(pattern.getContainer());

        this.isProcessing = false;
    }
    public void initialize(int quantity, NonNullList<ItemStack> took, IStackList<ItemStack> toExtract,Map<NonNullList<ItemStack>, Integer> counts){
        this.took = took;
        this.toExtractTotal = toExtract;
        this.quantity = quantity;
        this.counts = counts;
        isInitialized = true;
    }

    public Crafting(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.toExtract = CraftingTask.readMappedStackList(tag.getCompoundTag(NBT_ITEMS_TO_PUT));
        this.root = tag.getBoolean(NBT_ROOT);
        this.took = NonNullList.create();

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompoundTagAt(i));

            // Can be empty.
            took.add(stack);
        }

        // THIS ONLY WORKS IN 1.12.2 where recipes need a restart to get reloaded
        // In 1.14.x reloading of recipes works again but there might be an event we can catch
        if (isRecipeStillValid(took, pattern.getOutputs().get(0), network.world())) {
            this.quantity = tag.getInteger(NBT_QUANTITY);
        }
        this.containers = CraftingTask.readContainerList(tag.getTagList(NBT_CONTAINERS, Constants.NBT.TAG_COMPOUND), network.world());
        this.crafted = tag.getInteger(NBT_CRAFTED);


    }

    private boolean isRecipeStillValid(NonNullList<ItemStack> took, ItemStack output, World world) {
        InventoryCrafting inv = new CraftingPattern.InventoryCraftingDummy();
        for (int i = 0; i < took.size(); ++i) {
            inv.setInventorySlotContents(i, took.get(i));
        }
        for (IRecipe r : CraftingManager.REGISTRY) {
            if (r.matches(inv, world)) {
                if (r.getCraftingResult(inv).isItemEqual(output)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void finishCalculation() {
        toExtract = CraftingTask.calculateItemsToPut(toExtractTotal, counts);
    }

    public void addToExtractTotal(ItemStack stack) {
        toExtractTotal.add(stack);
    }
    
    public NonNullList<ItemStack> getTook() {
        return took;
    }

    public IStackList<ItemStack> getToExtract(boolean simulate) {
        int current = 0;
        for (Map.Entry<IStackList<ItemStack>, Integer> entry : toExtract.entrySet()) {
            current += entry.getValue();
            if (crafted < current) {
                if (!simulate) {
                    crafted++;
                }
                return entry.getKey();
            }

        }
        return API.instance().createItemStackList();
    }
    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = super.writeToNbt();

        tag.setTag(NBT_ITEMS_TO_PUT, CraftingTask.writeMappedStackList(toExtract));
        NBTTagList tookList = new NBTTagList();
        for (ItemStack took : this.took) {
            tookList.appendTag(StackUtils.serializeStackToNbt(took));
        }
        tag.setTag(NBT_TOOK, tookList);
        tag.setTag(NBT_CONTAINERS, CraftingTask.writeContainerList(containers));
        tag.setInteger(NBT_CRAFTED, crafted);

        return tag;
    }
}

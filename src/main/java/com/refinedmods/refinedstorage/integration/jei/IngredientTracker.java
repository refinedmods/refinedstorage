package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.IGridView;
import com.refinedmods.refinedstorage.util.ItemStackKey;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class IngredientTracker {

    private static IngredientTracker INSTANCE;

    private final Map<ItemStackKey, Integer> storedItems = new HashMap<>();
    private final Map<ItemStackKey, Integer> patternItems = new HashMap<>();
    private final Map<ItemStackKey, UUID> craftableItems = new HashMap<>();

    public static IngredientTracker getTracker(GridContainerMenu gridContainer) {
        if (INSTANCE == null) {
            INSTANCE = new IngredientTracker(gridContainer);
        }
        return INSTANCE;
    }

    public static void invalidate() {
        INSTANCE = null;
    }

    public IngredientTracker(GridContainerMenu gridContainer) {

        // Using IGridView#getStacks will return a *filtered* list of items in the view,
        // which will cause problems - especially if the user uses JEI synchronised searching.
        // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
        IGridView view = ((GridScreen) gridContainer.getScreenInfoProvider()).getView();

        //Existing stacks are synced by the fact that they are both referencing the same object. However, new stacks need to be added.
        view.addDeltaListener((iGridStack -> {
            if (iGridStack instanceof ItemGridStack stack) {
                if (stack.isCraftable()) {
                    craftableItems.put(new ItemStackKey(stack.getStack()), stack.getId());
                } else {
                    addStack(stack.getStack());
                }

            }
        }));
        Collection<IGridStack> gridStacks = view.getAllStacks();

        // Check grid
        if (gridContainer.getGrid().isGridActive()) {
            for (IGridStack gridStack : gridStacks) {
                if (gridStack instanceof ItemGridStack stackInGrid) {

                    // for craftables we should easily be able to take the hit from hashing the nbt
                    if (stackInGrid.isCraftable()) {
                        craftableItems.put(new ItemStackKey(stackInGrid.getStack()), gridStack.getId());
                    } else { // for non-craftables we don't hash nbt to avoid the performance hit
                        addStack(stackInGrid.getStack());
                    }
                }
            }
        }
    }

    public void addStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof ICraftingPatternProvider) {
            ICraftingPattern pattern = PatternItem.fromCache(Minecraft.getInstance().level, stack);
            if (pattern.isValid()) {
                for (ItemStack outputStack : pattern.getOutputs()) {
                    patternItems.merge(new ItemStackKey(outputStack), 1, Integer::sum);
                }
            }

        } else {
            storedItems.merge(new ItemStackKey(stack), stack.getCount(), Integer::sum);
        }

    }


    public ItemStack findBestMatch(GridContainerMenu gridContainer, Player player, List<ItemStack> list) {
        ItemStack resultStack = ItemStack.EMPTY;
        int count = 0;


        for (ItemStack listStack : list) {

            //check crafting matrix
            if (gridContainer.getGrid().getGridType().equals(GridType.CRAFTING)) {
                CraftingContainer craftingMatrix = gridContainer.getGrid().getCraftingMatrix();
                if (craftingMatrix != null) {
                    for (int matrixSlot = 0; matrixSlot < craftingMatrix.getContainerSize(); matrixSlot++) {
                        ItemStack stackInSlot = craftingMatrix.getItem(matrixSlot);
                        if (API.instance().getComparer().isEqual(listStack, stackInSlot, IComparer.COMPARE_NBT)) {
                            if (stackInSlot.getCount() > count) {
                                count = stackInSlot.getCount();
                                resultStack = stackInSlot;
                            }
                        }
                    }
                }
            }

            //check inventory
            for (int inventorySlot = 0; inventorySlot < player.getInventory().getContainerSize(); inventorySlot++) {
                ItemStack stackInSlot = player.getInventory().getItem(inventorySlot);
                if (API.instance().getComparer().isEqual(listStack, stackInSlot, IComparer.COMPARE_NBT)) {
                    if (stackInSlot.getCount() > count) {
                        count = stackInSlot.getCount();
                        resultStack = stackInSlot;
                    }
                }
            }

            //check storage
            var stored = storedItems.get(new ItemStackKey(listStack));
            if (stored != null) {
                if (stored > count) {
                    resultStack = listStack;
                    count = stored;
                }
            }
        }

        //If the item is not in storage check patterns autocrafting
        if (count == 0) {
            for (ItemStack itemStack : list) {
                ItemStackKey key = new ItemStackKey(itemStack);
                if (craftableItems.get(key) != null) {
                    resultStack = itemStack;
                    break;
                } else if (patternItems.get(key) != null) {
                    resultStack = itemStack;
                    break;
                }
            }
        }

        return resultStack;
    }

    public void updateAvailability(Ingredient.IngredientList ingredientList, GridContainerMenu gridContainer, Player player) {
        Map<Integer, Integer> usedMatrixStacks = new HashMap<>();
        Map<Integer, Integer> usedInventoryStacks = new HashMap<>();
        Map<ItemStackKey, Integer> usedStoredStacks = new HashMap<>();

        //Gather available Stacks
        for (Ingredient ingredient : ingredientList.ingredients) {
            ingredient.getSlotView().getIngredients(VanillaTypes.ITEM_STACK).takeWhile(stack -> !ingredient.isAvailable()).forEach(stack -> {

                if(ingredient.getCraftStackId() == null) {
                    ingredient.setCraftStackId(craftableItems.get(new ItemStackKey(stack)));
                }
                // Check grid crafting slots
                if (gridContainer.getGrid().getGridType().equals(GridType.CRAFTING)) {
                    CraftingContainer craftingMatrix = gridContainer.getGrid().getCraftingMatrix();
                    if (craftingMatrix != null) {
                        for (int matrixSlot = 0; matrixSlot < craftingMatrix.getContainerSize(); matrixSlot++) {
                            if (checkStack(usedMatrixStacks, ingredient, stack, matrixSlot, craftingMatrix.getItem(matrixSlot))) {
                                return;
                            }
                        }
                    }
                }

                //read inventory
                for (int inventorySlot = 0; inventorySlot < player.getInventory().getContainerSize(); inventorySlot++) {
                    if (checkStack(usedInventoryStacks, ingredient, stack, inventorySlot, player.getInventory().getItem(inventorySlot))) {
                        return;
                    }
                }

                //Check Stored Stacks
                ItemStackKey key = new ItemStackKey(stack);
                Integer stored = storedItems.get(key);
                if (stored != null) {
                    Integer used = usedStoredStacks.get(key);
                    int amount = Math.min(ingredient.getMissingAmount(), used == null ? stored : stored - used);
                    if (amount > 0) {
                        ingredient.fulfill(amount);
                        usedStoredStacks.put(key, used == null ? amount : used + amount);
                    }
                }
            });
        }
    }

    private boolean checkStack(Map<Integer, Integer> usedMatrixStacks, Ingredient ingredient, ItemStack stack, int slot, ItemStack stackInSlot) {
        if (API.instance().getComparer().isEqual(stack, stackInSlot, IComparer.COMPARE_NBT)) {
            Integer used = usedMatrixStacks.get(slot);
            int amount = Math.min(ingredient.getMissingAmount(), used == null ? stackInSlot.getCount() : stackInSlot.getCount() - used);
            if (amount > 0) {
                ingredient.fulfill(amount);
                usedMatrixStacks.put(slot, used == null ? amount : used + amount);
            }
            return ingredient.isAvailable();
        }
        return false;
    }

}

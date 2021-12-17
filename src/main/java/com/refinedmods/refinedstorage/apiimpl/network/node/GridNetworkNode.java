package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.grid.*;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.AllowedTagList;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.FluidGridStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.ItemGridStorageCacheListener;
import com.refinedmods.refinedstorage.block.GridBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridNetworkNode extends NetworkNode implements INetworkAwareGrid, IType {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid");
    public static final ResourceLocation CRAFTING_ID = new ResourceLocation(RS.ID, "crafting_grid");
    public static final ResourceLocation PATTERN_ID = new ResourceLocation(RS.ID, "pattern_grid");
    public static final ResourceLocation FLUID_ID = new ResourceLocation(RS.ID, "fluid_grid");

    public static final String NBT_VIEW_TYPE = "ViewType";
    public static final String NBT_SORTING_DIRECTION = "SortingDirection";
    public static final String NBT_SORTING_TYPE = "SortingType";
    public static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";
    public static final String NBT_TAB_SELECTED = "TabSelected";
    public static final String NBT_TAB_PAGE = "TabPage";
    public static final String NBT_SIZE = "Size";
    public static final int PROCESSING_MATRIX_SIZE = 81;
    private static final String NBT_EXACT_MODE = "Exact";
    private static final String NBT_PROCESSING_PATTERN = "ProcessingPattern";
    private static final String NBT_PROCESSING_TYPE = "ProcessingType";
    private static final String NBT_PROCESSING_MATRIX_FLUIDS = "ProcessingMatrixFluids";
    private static final String NBT_ALLOWED_TAGS = "AllowedTags";
    private final AllowedTagList allowedTagList = new AllowedTagList(this::updateAllowedTags, PROCESSING_MATRIX_SIZE);
    private final ResultContainer result = new ResultContainer();
    private final BaseItemHandler processingMatrix = new BaseItemHandler(PROCESSING_MATRIX_SIZE * 2)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading && slot < PROCESSING_MATRIX_SIZE) {
                allowedTagList.clearItemTags(slot);
            }
        });
    private final AbstractContainerMenu craftingContainer = new AbstractContainerMenu(MenuType.CRAFTING, 0) {
        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void slotsChanged(Container inventory) {
            if (!level.isClientSide) {
                onCraftingMatrixChanged();
            }
        }
    };
    private final FluidInventory processingMatrixFluids = new FluidInventory(PROCESSING_MATRIX_SIZE * 2, FluidAttributes.BUCKET_VOLUME * 64)
        .addListener(new NetworkNodeFluidInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading && slot < PROCESSING_MATRIX_SIZE) {
                allowedTagList.clearFluidTags(slot);
            }
        });
    private final Set<ICraftingGridListener> craftingListeners = new HashSet<>();
    private final List<IFilter> filters = new ArrayList<>();
    private final CraftingContainer matrix = new CraftingContainer(craftingContainer, 3, 3);
    private final List<IGridTab> tabs = new ArrayList<>();
    private final FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs).addListener(new NetworkNodeInventoryListener(this));
    private final GridType type;
    private CraftingRecipe currentRecipe;
    private boolean readingInventory;
    private int viewType = VIEW_TYPE_NORMAL;
    private int sortingDirection = SORTING_DIRECTION_DESCENDING;
    private final BaseItemHandler patterns = new BaseItemHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == 1 ? 1 : super.getSlotLimit(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            // Allow in slot 0
            // Disallow in slot 1
            // Only allow in slot 1 when it isn't a blank pattern
            // This makes it so that written patterns can be re-inserted in slot 1 to be overwritten again
            // This makes it so that blank patterns can't be inserted in slot 1 through hoppers.
            if (slot == 0 || stack.getTag() != null) {
                return super.insertItem(slot, stack, simulate);
            }

            return stack;
        }
    }
        .addValidator(new ItemValidator(RSItems.PATTERN.get()))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener(((handler, slot, reading) -> {
            ItemStack pattern = handler.getStackInSlot(slot);

            if (!reading && slot == 1 && !pattern.isEmpty()) {
                boolean processing = PatternItem.isProcessing(pattern);

                if (processing) {
                    for (int i = 0; i < PROCESSING_MATRIX_SIZE; ++i) {
                        processingMatrix.setStackInSlot(i, PatternItem.getInputSlot(pattern, i));
                        processingMatrixFluids.setFluid(i, PatternItem.getFluidInputSlot(pattern, i));
                        processingMatrix.setStackInSlot(PROCESSING_MATRIX_SIZE + i, PatternItem.getOutputSlot(pattern, i));
                        processingMatrixFluids.setFluid(PROCESSING_MATRIX_SIZE + i, PatternItem.getFluidOutputSlot(pattern, i));
                    }

                    AllowedTagList allowedTagsFromPattern = PatternItem.getAllowedTags(pattern);

                    if (allowedTagsFromPattern != null) {
                        allowedTagList.setAllowedItemTags(allowedTagsFromPattern.getAllowedItemTags());
                        allowedTagList.setAllowedFluidTags(allowedTagsFromPattern.getAllowedFluidTags());
                    }
                } else {
                    for (int i = 0; i < 9; ++i) {
                        matrix.setItem(i, PatternItem.getInputSlot(pattern, i));
                    }
                }

                setProcessingPattern(processing);
                markDirty();
            }
        }));
    private int sortingType = SORTING_TYPE_QUANTITY;
    private int searchBoxMode = SEARCH_BOX_MODE_NORMAL;
    private int size = SIZE_STRETCH;
    private int tabSelected = -1;
    private int tabPage = 0;
    private boolean exactPattern = true;
    private boolean processingPattern = false;
    private int processingType = IType.ITEMS;

    public GridNetworkNode(Level level, BlockPos pos, GridType type) {
        super(level, pos);

        this.type = type;
    }

    public static ResourceLocation getId(GridType type) {
        switch (type) {
            case NORMAL:
                return ID;
            case CRAFTING:
                return CRAFTING_ID;
            case PATTERN:
                return PATTERN_ID;
            case FLUID:
                return FLUID_ID;
            default:
                throw new IllegalArgumentException("Unknown grid type " + type);
        }
    }

    public AllowedTagList getAllowedTagList() {
        return allowedTagList;
    }

    private void updateAllowedTags() {
        markDirty();

        if (WorldUtils.getLoadedBlockEntity(level, pos) instanceof GridBlockEntity gridBlockEntity) {
            gridBlockEntity.getDataManager().sendParameterToWatchers(GridBlockEntity.ALLOWED_ITEM_TAGS);
            gridBlockEntity.getDataManager().sendParameterToWatchers(GridBlockEntity.ALLOWED_FLUID_TAGS);
        }
    }

    @Override
    public int getEnergyUsage() {
        switch (type) {
            case NORMAL:
                return RS.SERVER_CONFIG.getGrid().getGridUsage();
            case CRAFTING:
                return RS.SERVER_CONFIG.getGrid().getCraftingGridUsage();
            case PATTERN:
                return RS.SERVER_CONFIG.getGrid().getPatternGridUsage();
            case FLUID:
                return RS.SERVER_CONFIG.getGrid().getFluidGridUsage();
            default:
                return 0;
        }
    }

    public boolean isExactPattern() {
        return exactPattern;
    }

    public void setExactPattern(boolean exactPattern) {
        this.exactPattern = exactPattern;
    }

    public boolean isProcessingPattern() {
        return level.isClientSide ? GridBlockEntity.PROCESSING_PATTERN.getValue() : processingPattern;
    }

    public void setProcessingPattern(boolean processingPattern) {
        this.processingPattern = processingPattern;
    }

    @Override
    public GridType getGridType() {
        return type;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayer player) {
        return type == GridType.FLUID ? new FluidGridStorageCacheListener(player, network) : new ItemGridStorageCacheListener(player, network);
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        if (network != null) {
            return type == GridType.FLUID ? network.getFluidStorageCache() : network.getItemStorageCache();
        }

        return null;
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return network != null ? network.getItemGridHandler() : null;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        return network != null ? network.getFluidGridHandler() : null;
    }

    @Override
    public void addCraftingListener(ICraftingGridListener listener) {
        craftingListeners.add(listener);
    }

    @Override
    public void removeCraftingListener(ICraftingGridListener listener) {
        craftingListeners.remove(listener);
    }

    @Override
    public Component getTitle() {
        switch (type) {
            case CRAFTING:
                return new TranslatableComponent("gui.refinedstorage.crafting_grid");
            case PATTERN:
                return new TranslatableComponent("gui.refinedstorage.pattern_grid");
            case FLUID:
                return new TranslatableComponent("gui.refinedstorage.fluid_grid");
            default:
                return new TranslatableComponent("gui.refinedstorage.grid");
        }
    }

    public IItemHandler getPatterns() {
        return patterns;
    }

    @Override
    public IItemHandlerModifiable getFilter() {
        return filter;
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public List<IGridTab> getTabs() {
        return tabs;
    }

    @Override
    public CraftingContainer getCraftingMatrix() {
        return matrix;
    }

    @Override
    public ResultContainer getCraftingResult() {
        return result;
    }

    public BaseItemHandler getProcessingMatrix() {
        return processingMatrix;
    }

    public FluidInventory getProcessingMatrixFluids() {
        return processingMatrixFluids;
    }

    @Override
    public void onCraftingMatrixChanged() {
        if (currentRecipe == null || !currentRecipe.matches(matrix, level)) {
            currentRecipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, matrix, level).orElse(null);
        }

        if (currentRecipe == null) {
            result.setItem(0, ItemStack.EMPTY);
        } else {
            result.setItem(0, currentRecipe.assemble(matrix));
        }

        craftingListeners.forEach(ICraftingGridListener::onCraftingMatrixChanged);

        if (!readingInventory) {
            markDirty();
        }
    }

    @Override
    public void onRecipeTransfer(Player player, ItemStack[][] recipe) {
        API.instance().getCraftingGridBehavior().onRecipeTransfer(this, player, recipe);
    }

    public void clearMatrix() {
        for (int i = 0; i < processingMatrix.getSlots(); ++i) {
            processingMatrix.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < processingMatrixFluids.getSlots(); ++i) {
            processingMatrixFluids.setFluid(i, FluidStack.EMPTY);
        }

        for (int i = 0; i < matrix.getContainerSize(); ++i) {
            matrix.setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void onClosed(Player player) {
        // NO OP
    }

    @Override
    public boolean isGridActive() {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof GridBlock) {
            return state.getValue(NetworkNodeBlock.CONNECTED);
        }

        return false;
    }

    @Override
    public int getSlotId() {
        return -1;
    }

    @Override
    public void onCrafted(Player player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems) {
        API.instance().getCraftingGridBehavior().onCrafted(this, currentRecipe, player, availableItems, usedItems);
    }

    @Override
    public void onClear(Player player) {
        if (type == GridType.CRAFTING) {
            if (network != null && network.canRun() && network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
                for (int i = 0; i < matrix.getContainerSize(); ++i) {
                    ItemStack slot = matrix.getItem(i);

                    if (!slot.isEmpty()) {
                        matrix.setItem(i, network.insertItem(slot, slot.getCount(), Action.PERFORM));

                        network.getItemStorageTracker().changed(player, slot.copy());
                    }
                }
            } else {
                for (int i = 0; i < matrix.getContainerSize(); i++) {
                    ItemStack slot = matrix.getItem(i);

                    if (!slot.isEmpty()) {
                        player.getInventory().add(matrix.getItem(i));
                    }

                    onCraftingMatrixChanged();
                }
            }
        } else if (type == GridType.PATTERN) {
            clearMatrix();
        }

    }

    @Override
    public void onCraftedShift(Player player) {
        API.instance().getCraftingGridBehavior().onCraftedShift(this, player);
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            if (patterns.getStackInSlot(1).isEmpty()) {
                patterns.extractItem(0, 1, false);
            }

            ItemStack pattern = new ItemStack(RSItems.PATTERN.get());

            PatternItem.setToCurrentVersion(pattern);
            PatternItem.setProcessing(pattern, processingPattern);

            if (!processingPattern) {
                PatternItem.setExact(pattern, exactPattern);
            } else {
                PatternItem.setAllowedTags(pattern, allowedTagList);
            }

            if (processingPattern) {
                for (int i = 0; i < processingMatrix.getSlots(); ++i) {
                    if (!processingMatrix.getStackInSlot(i).isEmpty()) {
                        if (i >= PROCESSING_MATRIX_SIZE) {
                            PatternItem.setOutputSlot(pattern, i - PROCESSING_MATRIX_SIZE, processingMatrix.getStackInSlot(i));
                        } else {
                            PatternItem.setInputSlot(pattern, i, processingMatrix.getStackInSlot(i));
                        }
                    }

                    FluidStack fluid = processingMatrixFluids.getFluid(i);
                    if (!fluid.isEmpty()) {
                        if (i >= PROCESSING_MATRIX_SIZE) {
                            PatternItem.setFluidOutputSlot(pattern, i - PROCESSING_MATRIX_SIZE, fluid);
                        } else {
                            PatternItem.setFluidInputSlot(pattern, i, fluid);
                        }
                    }
                }
            } else {
                for (int i = 0; i < 9; ++i) {
                    ItemStack ingredient = matrix.getItem(i);

                    if (!ingredient.isEmpty()) {
                        PatternItem.setInputSlot(pattern, i, ingredient);
                    }
                }
            }

            patterns.setStackInSlot(1, pattern);
        }
    }

    private boolean isPatternAvailable() {
        return !(patterns.getStackInSlot(0).isEmpty() && patterns.getStackInSlot(1).isEmpty());
    }

    public boolean canCreatePattern() {
        if (!isPatternAvailable()) {
            return false;
        }

        if (isProcessingPattern()) {
            int inputsFilled = 0;
            int outputsFilled = 0;

            for (int i = 0; i < PROCESSING_MATRIX_SIZE; ++i) {
                if (!processingMatrix.getStackInSlot(i).isEmpty()) {
                    inputsFilled++;
                }

                if (!processingMatrixFluids.getFluid(i).isEmpty()) {
                    inputsFilled++;
                }
            }

            for (int i = PROCESSING_MATRIX_SIZE; i < processingMatrix.getSlots(); ++i) {
                if (!processingMatrix.getStackInSlot(i).isEmpty()) {
                    outputsFilled++;
                }

                if (!processingMatrixFluids.getFluid(i).isEmpty()) {
                    outputsFilled++;
                }
            }

            return inputsFilled > 0 && outputsFilled > 0;
        } else {
            return !result.getItem(0).isEmpty() && isPatternAvailable();
        }
    }

    @Override
    public int getViewType() {
        return level.isClientSide ? GridBlockEntity.VIEW_TYPE.getValue() : viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getSortingDirection() {
        return level.isClientSide ? GridBlockEntity.SORTING_DIRECTION.getValue() : sortingDirection;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    @Override
    public int getSortingType() {
        return level.isClientSide ? GridBlockEntity.SORTING_TYPE.getValue() : sortingType;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    @Override
    public int getSearchBoxMode() {
        return level.isClientSide ? GridBlockEntity.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public int getSize() {
        return level.isClientSide ? GridBlockEntity.SIZE.getValue() : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getTabSelected() {
        return level.isClientSide ? GridBlockEntity.TAB_SELECTED.getValue() : tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    @Override
    public int getTabPage() {
        return level.isClientSide ? GridBlockEntity.TAB_PAGE.getValue() : Math.min(tabPage, getTotalTabPages());
    }

    public void setTabPage(int page) {
        this.tabPage = page;
    }

    @Override
    public int getTotalTabPages() {
        return (int) Math.floor((float) Math.max(0, tabs.size() - 1) / (float) IGrid.TABS_PER_PAGE);
    }

    @Override
    public void onViewTypeChanged(int type) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.VIEW_TYPE, type);
    }

    @Override
    public void onSortingTypeChanged(int type) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public void onSizeChanged(int size) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.SIZE, size);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        BlockEntitySynchronizationManager.setParameter(GridBlockEntity.TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            BlockEntitySynchronizationManager.setParameter(GridBlockEntity.TAB_PAGE, page);
        }
    }

    @Override
    public int getType() {
        return level.isClientSide ? GridBlockEntity.PROCESSING_TYPE.getValue() : processingType;
    }

    @Override
    public void setType(int type) {
        this.processingType = type;

        this.markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return getProcessingMatrix();
    }

    @Override
    public FluidInventory getFluidFilters() {
        return getProcessingMatrixFluids();
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(NBT_ALLOWED_TAGS)) {
            allowedTagList.readFromNbt(tag.getCompound(NBT_ALLOWED_TAGS));
        }

        readingInventory = true;

        StackUtils.readItems(matrix, 0, tag);
        StackUtils.readItems(patterns, 1, tag);
        StackUtils.readItems(filter, 2, tag);
        StackUtils.readItems(processingMatrix, 3, tag);

        if (tag.contains(NBT_PROCESSING_MATRIX_FLUIDS)) {
            processingMatrixFluids.readFromNbt(tag.getCompound(NBT_PROCESSING_MATRIX_FLUIDS));
        }

        if (tag.contains(NBT_TAB_SELECTED)) {
            tabSelected = tag.getInt(NBT_TAB_SELECTED);
        }

        if (tag.contains(NBT_TAB_PAGE)) {
            tabPage = tag.getInt(NBT_TAB_PAGE);
        }

        readingInventory = false;
    }

    @Override
    public ResourceLocation getId() {
        return getId(type);
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.put(NBT_ALLOWED_TAGS, allowedTagList.writeToNbt());

        StackUtils.writeItems(matrix, 0, tag);
        StackUtils.writeItems(patterns, 1, tag);
        StackUtils.writeItems(filter, 2, tag);
        StackUtils.writeItems(processingMatrix, 3, tag);

        tag.put(NBT_PROCESSING_MATRIX_FLUIDS, processingMatrixFluids.writeToNbt());
        tag.putInt(NBT_TAB_SELECTED, tabSelected);
        tag.putInt(NBT_TAB_PAGE, tabPage);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_VIEW_TYPE, viewType);
        tag.putInt(NBT_SORTING_DIRECTION, sortingDirection);
        tag.putInt(NBT_SORTING_TYPE, sortingType);
        tag.putInt(NBT_SEARCH_BOX_MODE, searchBoxMode);
        tag.putInt(NBT_SIZE, size);

        tag.putBoolean(NBT_EXACT_MODE, exactPattern);
        tag.putBoolean(NBT_PROCESSING_PATTERN, processingPattern);
        tag.putInt(NBT_PROCESSING_TYPE, processingType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_VIEW_TYPE)) {
            viewType = tag.getInt(NBT_VIEW_TYPE);
        }

        if (tag.contains(NBT_SORTING_DIRECTION)) {
            sortingDirection = tag.getInt(NBT_SORTING_DIRECTION);
        }

        if (tag.contains(NBT_SORTING_TYPE)) {
            sortingType = tag.getInt(NBT_SORTING_TYPE);
        }

        if (tag.contains(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInt(NBT_SEARCH_BOX_MODE);
        }

        if (tag.contains(NBT_SIZE)) {
            size = tag.getInt(NBT_SIZE);
        }

        if (tag.contains(NBT_EXACT_MODE)) {
            exactPattern = tag.getBoolean(NBT_EXACT_MODE);
        }

        if (tag.contains(NBT_PROCESSING_PATTERN)) {
            processingPattern = tag.getBoolean(NBT_PROCESSING_PATTERN);
        }

        if (tag.contains(NBT_PROCESSING_TYPE)) {
            processingType = tag.getInt(NBT_PROCESSING_TYPE);
        }
    }

    @Override
    public IItemHandler getDrops() {
        switch (type) {
            case CRAFTING:
                return new CombinedInvWrapper(filter, new InvWrapper(matrix));
            case PATTERN:
                return new CombinedInvWrapper(filter, patterns);
            default:
                return new CombinedInvWrapper(filter);
        }
    }


}

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.block.BlockGrid;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.item.filter.FilterTab;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeContainer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

public class NetworkNodeGrid extends NetworkNode implements IGrid {
    public static final String ID = "grid";

    public static final String NBT_VIEW_TYPE = "ViewType";
    public static final String NBT_SORTING_DIRECTION = "SortingDirection";
    public static final String NBT_SORTING_TYPE = "SortingType";
    public static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";
    public static final String NBT_OREDICT_PATTERN = "OredictPattern";
    public static final String NBT_TAB_SELECTED = "TabSelected";
    public static final String NBT_SIZE = "Size";

    public static final int SORTING_DIRECTION_ASCENDING = 0;
    public static final int SORTING_DIRECTION_DESCENDING = 1;

    public static final int SORTING_TYPE_QUANTITY = 0;
    public static final int SORTING_TYPE_NAME = 1;
    public static final int SORTING_TYPE_ID = 2;

    public static final int SEARCH_BOX_MODE_NORMAL = 0;
    public static final int SEARCH_BOX_MODE_NORMAL_AUTOSELECTED = 1;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 2;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED = 3;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_NON_CRAFTABLES = 1;
    public static final int VIEW_TYPE_CRAFTABLES = 2;

    public static final int SIZE_STRETCH = 0;
    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_LARGE = 3;

    private Container craftingContainer = new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return false;
        }

        @Override
        public void onCraftMatrixChanged(IInventory inventory) {
            onCraftingMatrixChanged();
        }
    };
    private InventoryCrafting matrix = new InventoryCrafting(craftingContainer, 3, 3);
    private InventoryCraftResult result = new InventoryCraftResult();

    private ItemHandlerBase patterns = new ItemHandlerBase(2, new ItemHandlerListenerNetworkNode(this), new ItemValidatorBasic(RSItems.PATTERN));
    private List<Filter> filters = new ArrayList<>();
    private List<FilterTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, new ItemHandlerListenerNetworkNode(this));

    private GridType type;

    private int viewType = VIEW_TYPE_NORMAL;
    private int sortingDirection = SORTING_DIRECTION_DESCENDING;
    private int sortingType = SORTING_TYPE_QUANTITY;
    private int searchBoxMode = SEARCH_BOX_MODE_NORMAL;
    private int size = SIZE_STRETCH;

    private int tabSelected = -1;

    private boolean oredictPattern = false;

    public NetworkNodeGrid(INetworkNodeContainer container) {
        super(container);
    }

    @Override
    public int getEnergyUsage() {
        switch (getType()) {
            case NORMAL:
                return RS.INSTANCE.config.gridUsage;
            case CRAFTING:
                return RS.INSTANCE.config.craftingGridUsage;
            case PATTERN:
                return RS.INSTANCE.config.patternGridUsage;
            case FLUID:
                return RS.INSTANCE.config.fluidGridUsage;
            default:
                return 0;
        }
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isOredictPattern() {
        return oredictPattern;
    }

    public void setOredictPattern(boolean oredictPattern) {
        this.oredictPattern = oredictPattern;
    }

    public GridType getType() {
        if (type == null && container.world().getBlockState(container.pos()).getBlock() == RSBlocks.GRID) {
            type = (GridType) container.world().getBlockState(container.pos()).getValue(BlockGrid.TYPE);
        }

        return type == null ? GridType.NORMAL : type;
    }

    public void onOpened(EntityPlayer player) {
        if (network != null) {
            if (getType() == GridType.FLUID) {
                network.sendFluidStorageToClient((EntityPlayerMP) player);
            } else {
                network.sendItemStorageToClient((EntityPlayerMP) player);
            }
        }
    }

    @Override
    public String getGuiTitle() {
        return getType() == GridType.FLUID ? "gui.refinedstorage:fluid_grid" : "gui.refinedstorage:grid";
    }

    public IItemHandler getPatterns() {
        return patterns;
    }

    @Override
    public ItemHandlerBase getFilter() {
        return filter;
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public List<FilterTab> getTabs() {
        return tabs;
    }

    @Override
    public InventoryCrafting getCraftingMatrix() {
        return matrix;
    }

    @Override
    public InventoryCraftResult getCraftingResult() {
        return result;
    }

    @Override
    public void onCraftingMatrixChanged() {
        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, container.world()));

        markDirty();
    }

    @Override
    public void onRecipeTransfer(EntityPlayer player, ItemStack[][] recipe) {
        if (network != null && getType() == GridType.CRAFTING && !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        // First try to empty the crafting matrix
        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (!slot.isEmpty()) {
                // Only if we are a crafting grid. Pattern grids can just be emptied.
                if (getType() == GridType.CRAFTING) {
                    // If we are connected, try to insert into network. If it fails, stop.
                    if (network != null) {
                        if (network.insertItem(slot, slot.getCount(), true) != null) {
                            return;
                        } else {
                            network.insertItem(slot, slot.getCount(), false);
                        }
                    } else {
                        // If we aren't connected, try to insert into player inventory. If it fails, stop.
                        if (!player.inventory.addItemStackToInventory(slot.copy())) {
                            return;
                        }
                    }
                }

                matrix.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }

        // Now let's fill the matrix
        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            if (recipe[i] != null) {
                ItemStack[] possibilities = recipe[i];

                // If we are a crafting grid
                if (getType() == GridType.CRAFTING) {
                    boolean found = false;

                    // If we are connected, first try to get the possibilities from the network
                    if (network != null) {
                        for (ItemStack possibility : possibilities) {
                            ItemStack took = network.extractItem(possibility, 1, IComparer.COMPARE_NBT | IComparer.COMPARE_STRIP_NBT | (possibility.getItem().isDamageable() ? 0 : IComparer.COMPARE_DAMAGE), false);

                            if (took != null) {
                                matrix.setInventorySlotContents(i, RSUtils.transformNullToEmpty(took));

                                found = true;

                                break;
                            }
                        }
                    }

                    // If we haven't found anything in the network (or we are disconnected), go look in the player inventory
                    if (!found) {
                        for (ItemStack possibility : possibilities) {
                            for (int j = 0; j < player.inventory.getSizeInventory(); ++j) {
                                if (API.instance().getComparer().isEqual(possibility, player.inventory.getStackInSlot(j), IComparer.COMPARE_NBT | IComparer.COMPARE_STRIP_NBT | (possibility.getItem().isDamageable() ? 0 : IComparer.COMPARE_DAMAGE))) {
                                    matrix.setInventorySlotContents(i, ItemHandlerHelper.copyStackWithSize(player.inventory.getStackInSlot(j), 1));

                                    player.inventory.decrStackSize(j, 1);

                                    found = true;

                                    break;
                                }
                            }

                            if (found) {
                                break;
                            }
                        }
                    }
                } else if (getType() == GridType.PATTERN) {
                    // If we are a pattern grid we can just set the slot
                    matrix.setInventorySlotContents(i, possibilities[0]);
                }
            }
        }
    }

    @Override
    public void onClosed(EntityPlayer player) {
        // NO OP
    }

    @Override
    public void onCrafted(EntityPlayer player) {
        NonNullList<ItemStack> remainder = CraftingManager.getInstance().getRemainingItems(matrix, container.world());

        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                // If there is no space for the remainder, dump it in the player inventory
                if (!slot.isEmpty() && slot.getCount() > 1) {
                    if (!player.inventory.addItemStackToInventory(remainder.get(i).copy())) {
                        ItemStack remainderStack = network == null ? remainder.get(i).copy() : network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(), false);

                        if (remainderStack != null) {
                            InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainderStack);
                        }
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, remainder.get(i).copy());
                }
            } else if (!slot.isEmpty()) {
                if (slot.getCount() == 1 && network != null) {
                    matrix.setInventorySlotContents(i, RSUtils.transformNullToEmpty(network.extractItem(slot, 1, false)));
                } else {
                    matrix.decrStackSize(i, 1);
                }
            }
        }

        onCraftingMatrixChanged();
    }

    @Override
    public void onCraftedShift(EntityPlayer player) {
        List<ItemStack> craftedItemsList = new ArrayList<>();
        int craftedItems = 0;
        ItemStack crafted = result.getStackInSlot(0);

        while (true) {
            onCrafted(player);

            craftedItemsList.add(crafted.copy());

            craftedItems += crafted.getCount();

            if (!API.instance().getComparer().isEqual(crafted, result.getStackInSlot(0)) || craftedItems + crafted.getCount() > crafted.getMaxStackSize()) {
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                ItemStack remainder = network == null ? craftedItem : network.insertItem(craftedItem, craftedItem.getCount(), false);

                if (remainder != null) {
                    InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainder);
                }
            }
        }

        FMLCommonHandler.instance().firePlayerCraftingEvent(player, ItemHandlerHelper.copyStackWithSize(crafted, craftedItems), matrix);
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            patterns.extractItem(0, 1, false);

            ItemStack pattern = new ItemStack(RSItems.PATTERN);

            ItemPattern.setOredict(pattern, oredictPattern);

            for (int i = 0; i < 9; ++i) {
                ItemStack ingredient = matrix.getStackInSlot(i);

                if (!ingredient.isEmpty()) {
                    ItemPattern.setSlot(pattern, i, ingredient);
                }
            }

            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean canCreatePattern() {
        return !result.getStackInSlot(0).isEmpty() && patterns.getStackInSlot(1).isEmpty() && !patterns.getStackInSlot(0).isEmpty();
    }

    @Override
    public int getViewType() {
        return container.world().isRemote ? TileGrid.VIEW_TYPE.getValue() : viewType;
    }

    @Override
    public int getSortingDirection() {
        return container.world().isRemote ? TileGrid.SORTING_DIRECTION.getValue() : sortingDirection;
    }

    @Override
    public int getSortingType() {
        return container.world().isRemote ? TileGrid.SORTING_TYPE.getValue() : sortingType;
    }

    @Override
    public int getSearchBoxMode() {
        return container.world().isRemote ? TileGrid.SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    @Override
    public int getSize() {
        return container.world().isRemote ? TileGrid.SIZE.getValue() : size;
    }

    @Override
    public int getTabSelected() {
        return container.world().isRemote ? TileGrid.TAB_SELECTED.getValue() : tabSelected;
    }

    @Override
    public void onViewTypeChanged(int type) {
        TileDataManager.setParameter(TileGrid.VIEW_TYPE, type);
    }

    @Override
    public void onSortingTypeChanged(int type) {
        TileDataManager.setParameter(TileGrid.SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        TileDataManager.setParameter(TileGrid.SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        TileDataManager.setParameter(TileGrid.SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public void onSizeChanged(int size) {
        TileDataManager.setParameter(TileGrid.SIZE, size);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        TileDataManager.setParameter(TileGrid.TAB_SELECTED, tab);
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeConfig() {
        return TileGrid.REDSTONE_MODE;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItemsLegacy(matrix, 0, tag);
        RSUtils.readItems(patterns, 1, tag);
        RSUtils.readItems(filter, 2, tag);

        if (tag.hasKey(NBT_TAB_SELECTED)) {
            tabSelected = tag.getInteger(NBT_TAB_SELECTED);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItemsLegacy(matrix, 0, tag);
        RSUtils.writeItems(patterns, 1, tag);
        RSUtils.writeItems(filter, 2, tag);

        tag.setInteger(NBT_TAB_SELECTED, tabSelected);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_VIEW_TYPE, viewType);
        tag.setInteger(NBT_SORTING_DIRECTION, sortingDirection);
        tag.setInteger(NBT_SORTING_TYPE, sortingType);
        tag.setInteger(NBT_SEARCH_BOX_MODE, searchBoxMode);
        tag.setInteger(NBT_SIZE, size);

        tag.setBoolean(NBT_OREDICT_PATTERN, oredictPattern);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_VIEW_TYPE)) {
            viewType = tag.getInteger(NBT_VIEW_TYPE);
        }

        if (tag.hasKey(NBT_SORTING_DIRECTION)) {
            sortingDirection = tag.getInteger(NBT_SORTING_DIRECTION);
        }

        if (tag.hasKey(NBT_SORTING_TYPE)) {
            sortingType = tag.getInteger(NBT_SORTING_TYPE);
        }

        if (tag.hasKey(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInteger(NBT_SEARCH_BOX_MODE);
        }

        if (tag.hasKey(NBT_SIZE)) {
            size = tag.getInteger(NBT_SIZE);
        }

        if (tag.hasKey(NBT_OREDICT_PATTERN)) {
            oredictPattern = tag.getBoolean(NBT_OREDICT_PATTERN);
        }
    }

    @Override
    public IItemHandler getDrops() {
        switch (getType()) {
            case CRAFTING:
                return new CombinedInvWrapper(filter, new InvWrapper(matrix));
            case PATTERN:
                return new CombinedInvWrapper(filter, patterns);
            default:
                return new CombinedInvWrapper(filter);
        }
    }

    public static boolean isValidViewType(int type) {
        return type == VIEW_TYPE_NORMAL ||
            type == VIEW_TYPE_CRAFTABLES ||
            type == VIEW_TYPE_NON_CRAFTABLES;
    }

    public static boolean isValidSearchBoxMode(int mode) {
        return mode == SEARCH_BOX_MODE_NORMAL ||
            mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED ||
            mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED ||
            mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
    }

    public static boolean isSearchBoxModeWithAutoselection(int mode) {
        return mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
    }

    public static boolean isValidSortingType(int type) {
        return type == SORTING_TYPE_QUANTITY || type == SORTING_TYPE_NAME || type == SORTING_TYPE_ID;
    }

    public static boolean isValidSortingDirection(int direction) {
        return direction == SORTING_DIRECTION_ASCENDING || direction == SORTING_DIRECTION_DESCENDING;
    }

    public static boolean isValidSize(int size) {
        return size == SIZE_STRETCH ||
            size == SIZE_SMALL ||
            size == SIZE_MEDIUM ||
            size == SIZE_LARGE;
    }
}

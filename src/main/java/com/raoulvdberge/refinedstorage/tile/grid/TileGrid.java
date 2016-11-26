package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.block.BlockGrid;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerGridFilterInGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

public class TileGrid extends TileNode implements IGrid {
    public static final TileDataParameter<Integer> VIEW_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.viewType;
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (isValidViewType(value)) {
                tile.viewType = value;

                tile.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.sortingDirection;
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (isValidSortingDirection(value)) {
                tile.sortingDirection = value;

                tile.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.sortingType;
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (isValidSortingType(value)) {
                tile.sortingType = value;

                tile.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileGrid>() {
        @Override
        public Integer getValue(TileGrid tile) {
            return tile.searchBoxMode;
        }
    }, new ITileDataConsumer<Integer, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Integer value) {
            if (isValidSearchBoxMode(value)) {
                tile.searchBoxMode = value;

                tile.markDirty();
            }
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateSearchFieldFocus(parameter.getValue());
        }
    });

    public static final TileDataParameter<Boolean> OREDICT_PATTERN = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileGrid>() {
        @Override
        public Boolean getValue(TileGrid tile) {
            return tile.oredictPattern;
        }
    }, new ITileDataConsumer<Boolean, TileGrid>() {
        @Override
        public void setValue(TileGrid tile, Boolean value) {
            tile.oredictPattern = value;

            tile.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateOredictPattern(parameter.getValue());
        }
    });

    public static final String NBT_VIEW_TYPE = "ViewType";
    public static final String NBT_SORTING_DIRECTION = "SortingDirection";
    public static final String NBT_SORTING_TYPE = "SortingType";
    public static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";
    public static final String NBT_OREDICT_PATTERN = "OredictPattern";

    public static final int SORTING_DIRECTION_ASCENDING = 0;
    public static final int SORTING_DIRECTION_DESCENDING = 1;

    public static final int SORTING_TYPE_QUANTITY = 0;
    public static final int SORTING_TYPE_NAME = 1;

    public static final int SEARCH_BOX_MODE_NORMAL = 0;
    public static final int SEARCH_BOX_MODE_NORMAL_AUTOSELECTED = 1;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 2;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED = 3;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_NON_CRAFTABLES = 1;
    public static final int VIEW_TYPE_CRAFTABLES = 2;

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

    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RSItems.PATTERN));
    private List<GridFilteredItem> filteredItems = new ArrayList<>();
    private ItemHandlerGridFilterInGrid filter = new ItemHandlerGridFilterInGrid(filteredItems);

    private EnumGridType type;

    private int viewType = VIEW_TYPE_NORMAL;
    private int sortingDirection = SORTING_DIRECTION_DESCENDING;
    private int sortingType = SORTING_TYPE_QUANTITY;
    private int searchBoxMode = SEARCH_BOX_MODE_NORMAL;

    private boolean oredictPattern = false;

    public TileGrid() {
        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(OREDICT_PATTERN);
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

    @Override
    public void updateNode() {
    }

    public EnumGridType getType() {
        if (type == null && getWorld().getBlockState(pos).getBlock() == RSBlocks.GRID) {
            this.type = (EnumGridType) getWorld().getBlockState(pos).getValue(BlockGrid.TYPE);
        }

        return type == null ? EnumGridType.NORMAL : type;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return network != null ? network.getPosition() : null;
    }

    public void onOpened(EntityPlayer player) {
        if (isConnected()) {
            if (getType() == EnumGridType.FLUID) {
                network.sendFluidStorageToClient((EntityPlayerMP) player);
            } else {
                network.sendItemStorageToClient((EntityPlayerMP) player);
            }
        }
    }

    @Override
    public IItemGridHandler getItemHandler() {
        return connected ? network.getItemGridHandler() : null;
    }

    @Override
    public IFluidGridHandler getFluidHandler() {
        return connected ? network.getFluidGridHandler() : null;
    }

    @Override
    public String getGuiTitle() {
        return getType() == EnumGridType.FLUID ? "gui.refinedstorage:fluid_grid" : "gui.refinedstorage:grid";
    }

    public InventoryCrafting getMatrix() {
        return matrix;
    }

    public InventoryCraftResult getResult() {
        return result;
    }

    public IItemHandler getPatterns() {
        return patterns;
    }

    @Override
    public ItemHandlerBasic getFilter() {
        return filter;
    }

    @Override
    public List<GridFilteredItem> getFilteredItems() {
        return filteredItems;
    }

    public void onCraftingMatrixChanged() {
        markDirty();

        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, getWorld()));
    }

    public void onCrafted(EntityPlayer player) {
        NonNullList<ItemStack> remainder = CraftingManager.getInstance().getRemainingItems(matrix, getWorld());

        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (i < remainder.size() && !remainder.get(i).isEmpty()) {
                // If there is no space for the remainder, dump it in the player inventory
                if (slot != null && slot.getCount() > 1) {
                    if (!player.inventory.addItemStackToInventory(remainder.get(i).copy())) {
                        ItemStack remainderStack = network.insertItem(remainder.get(i).copy(), remainder.get(i).getCount(), false);

                        if (remainderStack != null) {
                            InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainderStack);
                        }
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, remainder.get(i).copy());
                }
            } else if (slot != null) {
                if (slot.getCount() == 1 && isConnected()) {
                    matrix.setInventorySlotContents(i, network.extractItem(slot, 1, false));
                } else {
                    matrix.decrStackSize(i, 1);
                }
            }
        }

        onCraftingMatrixChanged();
    }

    public void onCraftedShift(ContainerGrid container, EntityPlayer player) {
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
                ItemStack remainder = network.insertItem(craftedItem, craftedItem.getCount(), false);
                if (remainder != null) {
                    InventoryHelper.spawnItemStack(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainder);
                }
            }
        }

        container.sendCraftingSlots();
        container.detectAndSendChanges();
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            patterns.extractItem(0, 1, false);

            ItemStack pattern = new ItemStack(RSItems.PATTERN);

            ItemPattern.setOredict(pattern, oredictPattern);

            for (int i = 0; i < 9; ++i) {
                ItemStack ingredient = matrix.getStackInSlot(i);

                if (ingredient != null) {
                    ItemPattern.setSlot(pattern, i, ingredient);
                }
            }

            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean canCreatePattern() {
        return result.getStackInSlot(0) != null && patterns.getStackInSlot(1) == null && patterns.getStackInSlot(0) != null;
    }

    public void onRecipeTransfer(EntityPlayer player, ItemStack[][] recipe) {
        // First try to empty the crafting matrix
        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (slot != null) {
                // Only if we are a crafting grid. Pattern grids can just be emptied.
                if (getType() == EnumGridType.CRAFTING) {
                    // If we are connected, try to insert into network. If it fails, stop.
                    if (isConnected()) {
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

                matrix.setInventorySlotContents(i, null);
            }
        }

        // Now let's fill the matrix
        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            if (recipe[i] != null) {
                ItemStack[] possibilities = recipe[i];

                // If we are a crafting grid
                if (getType() == EnumGridType.CRAFTING) {
                    boolean found = false;

                    // If we are connected, first try to get the possibilities from the network
                    if (isConnected()) {
                        for (ItemStack possibility : possibilities) {
                            ItemStack took = network.extractItem(possibility, 1, false);

                            if (took != null) {
                                matrix.setInventorySlotContents(i, took);

                                found = true;

                                break;
                            }
                        }
                    }

                    // If we haven't found anything in the network (or we are disconnected), go look in the player inventory
                    if (!found) {
                        for (ItemStack possibility : possibilities) {
                            for (int j = 0; j < player.inventory.getSizeInventory(); ++j) {
                                if (API.instance().getComparer().isEqualNoQuantity(possibility, player.inventory.getStackInSlot(j))) {
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
                } else if (getType() == EnumGridType.PATTERN) {
                    // If we are a pattern grid we can just set the slot
                    matrix.setInventorySlotContents(i, possibilities[0]);
                }
            }
        }
    }

    @Override
    public int getViewType() {
        return getWorld().isRemote ? VIEW_TYPE.getValue() : viewType;
    }

    @Override
    public int getSortingDirection() {
        return getWorld().isRemote ? SORTING_DIRECTION.getValue() : sortingDirection;
    }

    @Override
    public int getSortingType() {
        return getWorld().isRemote ? SORTING_TYPE.getValue() : sortingType;
    }

    @Override
    public int getSearchBoxMode() {
        return getWorld().isRemote ? SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    @Override
    public void onViewTypeChanged(int type) {
        TileDataManager.setParameter(VIEW_TYPE, type);
    }

    @Override
    public void onSortingTypeChanged(int type) {
        TileDataManager.setParameter(SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        TileDataManager.setParameter(SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        TileDataManager.setParameter(SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeConfig() {
        return REDSTONE_MODE;
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
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItemsLegacy(matrix, 0, tag);
        RSUtils.writeItems(patterns, 1, tag);
        RSUtils.writeItems(filter, 2, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_VIEW_TYPE, viewType);
        tag.setInteger(NBT_SORTING_DIRECTION, sortingDirection);
        tag.setInteger(NBT_SORTING_TYPE, sortingType);
        tag.setInteger(NBT_SEARCH_BOX_MODE, searchBoxMode);

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
        return mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
    }

    public static boolean isValidSortingType(int type) {
        return type == SORTING_TYPE_QUANTITY || type == TileGrid.SORTING_TYPE_NAME;
    }

    public static boolean isValidSortingDirection(int direction) {
        return direction == SORTING_DIRECTION_ASCENDING || direction == SORTING_DIRECTION_DESCENDING;
    }
}

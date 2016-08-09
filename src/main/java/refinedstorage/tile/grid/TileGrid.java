package refinedstorage.tile.grid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.block.BlockGrid;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.grid.GridFilteredItem;
import refinedstorage.gui.grid.GuiGrid;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerGridFilterInGrid;
import refinedstorage.inventory.ItemValidatorBasic;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileNode;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

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
    }, parameter ->
        GuiGrid.markedForSorting = true);

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
    }, parameter -> GuiGrid.markedForSorting = true);

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
    }, parameter -> GuiGrid.markedForSorting = true);

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

    public static final String NBT_VIEW_TYPE = "ViewType";
    public static final String NBT_SORTING_DIRECTION = "SortingDirection";
    public static final String NBT_SORTING_TYPE = "SortingType";
    public static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

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

    private ItemHandlerBasic patterns = new ItemHandlerBasic(2, this, new ItemValidatorBasic(RefinedStorageItems.PATTERN));
    private List<GridFilteredItem> filteredItems = new ArrayList<>();
    private ItemHandlerGridFilterInGrid filter = new ItemHandlerGridFilterInGrid(filteredItems);

    private EnumGridType type;

    private int viewType = VIEW_TYPE_NORMAL;
    private int sortingDirection = SORTING_DIRECTION_DESCENDING;
    private int sortingType = SORTING_TYPE_NAME;
    private int searchBoxMode = SEARCH_BOX_MODE_NORMAL;

    public TileGrid() {
        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
    }

    @Override
    public int getEnergyUsage() {
        switch (getType()) {
            case NORMAL:
                return RefinedStorage.INSTANCE.gridUsage;
            case CRAFTING:
                return RefinedStorage.INSTANCE.craftingGridUsage;
            case PATTERN:
                return RefinedStorage.INSTANCE.patternGridUsage;
            default:
                return 0;
        }
    }

    @Override
    public void updateNode() {
    }

    public EnumGridType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.GRID) {
            this.type = (EnumGridType) worldObj.getBlockState(pos).getValue(BlockGrid.TYPE);
        }

        return type == null ? EnumGridType.NORMAL : type;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return network != null ? network.getPosition() : null;
    }

    public void onGridOpened(EntityPlayer player) {
        if (isConnected()) {
            network.sendStorageToClient((EntityPlayerMP) player);
        }
    }

    @Override
    public IGridHandler getGridHandler() {
        return isConnected() ? network.getGridHandler() : null;
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

        result.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(matrix, worldObj));
    }

    public void onCrafted(EntityPlayer player) {
        ItemStack[] remainder = CraftingManager.getInstance().getRemainingItems(matrix, worldObj);

        for (int i = 0; i < matrix.getSizeInventory(); ++i) {
            ItemStack slot = matrix.getStackInSlot(i);

            if (i < remainder.length && remainder[i] != null) {
                if (slot != null && slot.stackSize > 1) {
                    if (!player.inventory.addItemStackToInventory(remainder[i].copy())) {
                        InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), remainder[i].copy());
                    }

                    matrix.decrStackSize(i, 1);
                } else {
                    matrix.setInventorySlotContents(i, remainder[i].copy());
                }
            } else {
                if (slot != null) {
                    if (slot.stackSize == 1 && isConnected()) {
                        matrix.setInventorySlotContents(i, NetworkUtils.extractItem(network, slot, 1));
                    } else {
                        matrix.decrStackSize(i, 1);
                    }
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

            craftedItems += crafted.stackSize;

            if (!CompareUtils.compareStack(crafted, result.getStackInSlot(0)) || craftedItems + crafted.stackSize > crafted.getMaxStackSize()) {
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), craftedItem);
            }
        }

        container.sendCraftingSlots();
        container.detectAndSendChanges();
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            patterns.extractItem(0, 1, false);

            ItemStack pattern = new ItemStack(RefinedStorageItems.PATTERN);

            for (ItemStack byproduct : CraftingManager.getInstance().getRemainingItems(matrix, worldObj)) {
                if (byproduct != null) {
                    ItemPattern.addByproduct(pattern, byproduct);
                }
            }

            ItemPattern.addOutput(pattern, result.getStackInSlot(0));

            ItemPattern.setProcessing(pattern, false);

            for (int i = 0; i < 9; ++i) {
                ItemStack ingredient = matrix.getStackInSlot(i);

                if (ingredient != null) {
                    ItemPattern.addInput(pattern, ingredient);
                }
            }

            patterns.setStackInSlot(1, pattern);
        }
    }

    public boolean canCreatePattern() {
        return result.getStackInSlot(0) != null && patterns.getStackInSlot(1) == null && patterns.getStackInSlot(0) != null;
    }

    public void onRecipeTransfer(ItemStack[][] recipe) {
        if (isConnected()) {
            for (int i = 0; i < matrix.getSizeInventory(); ++i) {
                ItemStack slot = matrix.getStackInSlot(i);

                if (slot != null) {
                    if (getType() == EnumGridType.CRAFTING) {
                        if (network.insertItem(slot, slot.stackSize, true) != null) {
                            return;
                        } else {
                            network.insertItem(slot, slot.stackSize, false);
                        }
                    }

                    matrix.setInventorySlotContents(i, null);
                }
            }

            for (int i = 0; i < matrix.getSizeInventory(); ++i) {
                if (recipe[i] != null) {
                    ItemStack[] possibilities = recipe[i];

                    if (getType() == EnumGridType.CRAFTING) {
                        for (ItemStack possibility : possibilities) {
                            ItemStack took = NetworkUtils.extractItem(network, possibility, 1);

                            if (took != null) {
                                matrix.setInventorySlotContents(i, took);

                                break;
                            }
                        }
                    } else if (getType() == EnumGridType.PATTERN) {
                        matrix.setInventorySlotContents(i, possibilities[0]);
                    }
                }
            }
        }
    }

    @Override
    public int getViewType() {
        return worldObj.isRemote ? VIEW_TYPE.getValue() : viewType;
    }

    @Override
    public int getSortingDirection() {
        return worldObj.isRemote ? SORTING_DIRECTION.getValue() : sortingDirection;
    }

    @Override
    public int getSortingType() {
        return worldObj.isRemote ? SORTING_TYPE.getValue() : sortingType;
    }

    @Override
    public int getSearchBoxMode() {
        return worldObj.isRemote ? SEARCH_BOX_MODE.getValue() : searchBoxMode;
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItemsLegacy(matrix, 0, tag);
        readItems(patterns, 1, tag);
        readItems(filter, 2, tag);

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
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItemsLegacy(matrix, 0, tag);
        writeItems(patterns, 1, tag);
        writeItems(filter, 2, tag);

        tag.setInteger(NBT_VIEW_TYPE, viewType);
        tag.setInteger(NBT_SORTING_DIRECTION, sortingDirection);
        tag.setInteger(NBT_SORTING_TYPE, sortingType);
        tag.setInteger(NBT_SEARCH_BOX_MODE, searchBoxMode);

        return tag;
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

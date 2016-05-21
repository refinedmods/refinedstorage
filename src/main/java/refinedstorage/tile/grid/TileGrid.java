package refinedstorage.tile.grid;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.block.BlockGrid;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridCraftingStart;
import refinedstorage.network.MessageGridSettingsUpdate;
import refinedstorage.network.MessageGridStoragePull;
import refinedstorage.network.MessageGridStoragePush;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.ArrayList;
import java.util.List;

public class TileGrid extends TileMachine implements IGrid {
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
    private InventoryCrafting craftingInventory = new InventoryCrafting(craftingContainer, 3, 3);
    private InventoryCraftResult craftingResultInventory = new InventoryCraftResult();

    private InventorySimple patternsInventory = new InventorySimple("patterns", 2, this);

    private int sortingDirection = SORTING_DIRECTION_DESCENDING;
    private int sortingType = SORTING_TYPE_NAME;
    private int searchBoxMode = SEARCH_BOX_MODE_NORMAL;

    private List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();

    @Override
    public int getEnergyUsage() {
        return 4;
    }

    @Override
    public void updateMachine() {
    }

    public EnumGridType getType() {
        if (worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.GRID) {
            return (EnumGridType) worldObj.getBlockState(pos).getValue(BlockGrid.TYPE);
        }

        return EnumGridType.NORMAL;
    }

    @Override
    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    @Override
    public void onItemPush(int playerSlot, boolean one) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridStoragePush(getPos().getX(), getPos().getY(), getPos().getZ(), playerSlot, one));
    }

    @Override
    public void onItemPull(int id, int flags) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridStoragePull(getPos().getX(), getPos().getY(), getPos().getZ(), id, flags));
    }

    public InventoryCrafting getCraftingInventory() {
        return craftingInventory;
    }

    public InventoryCraftResult getCraftingResultInventory() {
        return craftingResultInventory;
    }

    public InventorySimple getPatternsInventory() {
        return patternsInventory;
    }

    public void onCraftingMatrixChanged() {
        markDirty();

        craftingResultInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingInventory, worldObj));
    }

    public void onCrafted(ContainerGrid container) {
        if (!worldObj.isRemote) {
            ItemStack[] remainder = CraftingManager.getInstance().func_180303_b(craftingInventory, worldObj);

            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                if (remainder[i] != null) {
                    craftingInventory.setInventorySlotContents(i, remainder[i].copy());
                } else {
                    ItemStack slot = craftingInventory.getStackInSlot(i);

                    if (slot != null) {
                        if (slot.stackSize == 1 && isConnected()) {
                            craftingInventory.setInventorySlotContents(i, controller.take(slot.copy()));
                        } else {
                            craftingInventory.decrStackSize(i, 1);
                        }
                    }
                }
            }

            onCraftingMatrixChanged();

            container.detectAndSendChanges();
        }
    }

    public void onCraftedShift(ContainerGrid container, EntityPlayer player) {
        List<ItemStack> craftedItemsList = new ArrayList<ItemStack>();
        int craftedItems = 0;
        ItemStack crafted = craftingResultInventory.getStackInSlot(0);

        while (true) {
            onCrafted(container);

            craftedItemsList.add(crafted.copy());

            craftedItems += crafted.stackSize;

            if (!RefinedStorageUtils.compareStack(crafted, craftingResultInventory.getStackInSlot(0)) || craftedItems + crafted.stackSize > crafted.getMaxStackSize()) {
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                if (controller != null && controller.push(craftedItem.copy())) {
                    // NO OP
                } else {
                    InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), craftedItem);
                }
            }
        }
    }

    public void onCreatePattern() {
        if (canCreatePattern()) {
            patternsInventory.decrStackSize(0, 1);

            ItemStack pattern = new ItemStack(RefinedStorageItems.PATTERN);

            ItemPattern.addOutput(pattern, craftingResultInventory.getStackInSlot(0));

            ItemPattern.setProcessing(pattern, false);

            for (int i = 0; i < 9; ++i) {
                ItemStack ingredient = craftingInventory.getStackInSlot(i);

                if (ingredient != null) {
                    ItemPattern.addInput(pattern, ingredient);
                }
            }

            patternsInventory.setInventorySlotContents(1, pattern);
        }
    }

    public boolean canCreatePattern() {
        return craftingResultInventory.getStackInSlot(0) != null && patternsInventory.getStackInSlot(1) == null && patternsInventory.getStackInSlot(0) != null;
    }

    public void onRecipeTransfer(ItemStack[][] recipe) {
        if (isConnected()) {
            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                ItemStack slot = craftingInventory.getStackInSlot(i);

                if (slot != null) {
                    if (getType() == EnumGridType.CRAFTING) {
                        if (!controller.push(slot)) {
                            return;
                        }
                    }

                    craftingInventory.setInventorySlotContents(i, null);
                }
            }

            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                if (recipe[i] != null) {
                    ItemStack[] possibilities = recipe[i];

                    if (getType() == EnumGridType.CRAFTING) {
                        for (ItemStack possibility : possibilities) {
                            ItemStack took = controller.take(possibility);

                            if (took != null) {
                                craftingInventory.setInventorySlotContents(i, possibility);

                                break;
                            }
                        }
                    } else if (getType() == EnumGridType.PATTERN) {
                        craftingInventory.setInventorySlotContents(i, possibilities[0]);
                    }
                }
            }
        }
    }

    public int getSortingDirection() {
        return sortingDirection;
    }

    public void setSortingDirection(int sortingDirection) {
        markDirty();

        this.sortingDirection = sortingDirection;
    }

    public int getSortingType() {
        return sortingType;
    }

    public void setSortingType(int sortingType) {
        markDirty();

        this.sortingType = sortingType;
    }

    public int getSearchBoxMode() {
        return searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        markDirty();

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridSettingsUpdate(this, sortingDirection, type, searchBoxMode));
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridSettingsUpdate(this, direction, sortingType, searchBoxMode));
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridSettingsUpdate(this, sortingDirection, sortingType, searchBoxMode));
    }

    @Override
    public void onCraftingRequested(int id, int quantity) {
        RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingStart(getPos().getX(), getPos().getY(), getPos().getZ(), id, quantity));
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeSetting() {
        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreInventory(craftingInventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(patternsInventory, 1, nbt);

        if (nbt.hasKey(NBT_SORTING_DIRECTION)) {
            sortingDirection = nbt.getInteger(NBT_SORTING_DIRECTION);
        }

        if (nbt.hasKey(NBT_SORTING_TYPE)) {
            sortingType = nbt.getInteger(NBT_SORTING_TYPE);
        }

        if (nbt.hasKey(NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = nbt.getInteger(NBT_SEARCH_BOX_MODE);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveInventory(craftingInventory, 0, nbt);
        RefinedStorageUtils.saveInventory(patternsInventory, 1, nbt);

        nbt.setInteger(NBT_SORTING_DIRECTION, sortingDirection);
        nbt.setInteger(NBT_SORTING_TYPE, sortingType);
        nbt.setInteger(NBT_SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);

        if (connected) {
            controller.sendItemGroups(buf);
        } else {
            buf.writeInt(0);
        }
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();

        List<ItemGroup> groups = new ArrayList<ItemGroup>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            groups.add(new ItemGroup(buf));
        }

        itemGroups = groups;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerGrid.class;
    }

    @Override
    public IInventory getDroppedInventory() {
        switch (getType()) {
            case CRAFTING:
                return craftingInventory;
            case PATTERN:
                return patternsInventory;
            default:
                return null;
        }
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

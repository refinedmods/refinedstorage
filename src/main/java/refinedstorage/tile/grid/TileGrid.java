package refinedstorage.tile.grid;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.BlockGrid;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.network.MessageGridSettingsUpdate;
import refinedstorage.network.MessageGridStoragePull;
import refinedstorage.network.MessageGridStoragePush;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.util.InventoryUtils;

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
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 1;

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
    private InventorySimple craftingResultInventory = new InventorySimple("crafting_result", 1);

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

    public InventorySimple getCraftingResultInventory() {
        return craftingResultInventory;
    }

    public void onCraftingMatrixChanged() {
        markDirty();

        craftingResultInventory.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingInventory, worldObj));
    }

    public void onCrafted(ContainerGrid container) {
        if (!worldObj.isRemote) {
            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                ItemStack slot = craftingInventory.getStackInSlot(i);

                if (slot != null) {
                    if (slot.stackSize == 1 && isConnected()) {
                        craftingInventory.setInventorySlotContents(i, controller.take(slot.copy()));
                    } else {
                        craftingInventory.decrStackSize(i, 1);
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

            if (!InventoryUtils.compareStack(crafted, craftingResultInventory.getStackInSlot(0)) || craftedItems + crafted.stackSize > 64) {
                break;
            }
        }

        for (ItemStack craftedItem : craftedItemsList) {
            if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
                if (controller != null && controller.push(craftedItem.copy())) {
                    // NO OP
                } else {
                    InventoryUtils.dropStack(player.worldObj, craftedItem, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                }
            }
        }
    }

    public void onRecipeTransfer(ItemStack[][] recipe) {
        if (isConnected()) {
            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                ItemStack slot = craftingInventory.getStackInSlot(i);

                if (slot != null) {
                    if (!controller.push(slot)) {
                        return;
                    }

                    craftingInventory.setInventorySlotContents(i, null);
                }
            }

            for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
                if (recipe[i] != null) {
                    ItemStack[] possibilities = recipe[i];

                    for (ItemStack possibility : possibilities) {
                        ItemStack took = controller.take(possibility);

                        if (took != null) {
                            craftingInventory.setInventorySlotContents(i, possibility);

                            break;
                        }
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
    public IRedstoneModeConfig getRedstoneModeSetting() {
        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        InventoryUtils.restoreInventory(craftingInventory, 0, nbt);

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

        InventoryUtils.saveInventory(craftingInventory, 0, nbt);

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
            buf.writeInt(controller.getItemGroups().size());

            for (ItemGroup group : controller.getItemGroups()) {
                group.toBytes(buf, controller.getItemGroups().indexOf(group));
            }
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
        if (getType() == EnumGridType.CRAFTING) {
            return craftingInventory;
        }

        return null;
    }
}

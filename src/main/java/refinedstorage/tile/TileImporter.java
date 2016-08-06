package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.data.TileDataParameter;

public class TileImporter extends TileMultipartNode implements IComparable, IFilterable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private int compare = 0;
    private int mode = IFilterable.WHITELIST;

    private int currentSlot;

    public TileImporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.importerUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        IItemHandler handler = getItemHandler(getFacingTile(), getDirection().getOpposite());

        if (getFacingTile() instanceof TileDiskDrive || handler == null) {
            return;
        }

        if (currentSlot >= handler.getSlots()) {
            currentSlot = 0;
        }

        if (handler.getSlots() > 0) {
            ItemStack stack = handler.getStackInSlot(currentSlot);

            if (stack == null || !IFilterable.canTake(filters, mode, compare, stack)) {
                currentSlot++;
            } else if (ticks % upgrades.getSpeed() == 0) {
                int quantity = upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1;

                ItemStack result = handler.extractItem(currentSlot, quantity, true);

                if (result != null && network.insertItem(result, result.stackSize, true) == null) {
                    network.insertItem(result, result.stackSize, false);

                    handler.extractItem(currentSlot, quantity, false);
                } else {
                    currentSlot++;
                }
            }
        }
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        readItems(filters, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        writeItems(filters, 0, tag);
        writeItems(upgrades, 1, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IItemHandler getFilters() {
        return filters;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return upgrades;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) upgrades;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

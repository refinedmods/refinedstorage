package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerImporter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.ModeConstants;
import refinedstorage.tile.config.ModeFilter;

public class TileImporter extends TileMachine implements ICompareConfig, IModeConfig {
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    private InventorySimple inventory = new InventorySimple("importer", 9, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    private int currentSlot;

    @Override
    public int getEnergyUsage() {
        return 2 + RefinedStorageUtils.getUpgradeEnergyUsage(upgradesInventory);
    }

    @Override
    public void updateMachine() {
        IItemHandler handler = RefinedStorageUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

        if (getFacingTile() instanceof TileDiskDrive || handler == null) {
            return;
        }

        if (currentSlot >= handler.getSlots()) {
            currentSlot = 0;
        }

        if (handler.getSlots() > 0) {
            ItemStack stack = handler.getStackInSlot(currentSlot);

            if (stack == null || !ModeFilter.respectsMode(inventory, this, compare, stack)) {
                currentSlot++;
            } else if (ticks % RefinedStorageUtils.getSpeed(upgradesInventory) == 0) {
                ItemStack result = handler.extractItem(currentSlot, 1, true);

                if (result != null && controller.push(result)) {
                    handler.extractItem(currentSlot, 1, false);
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
        RefinedStorageUtils.saveInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerImporter.class;
    }

    @Override
    public IInventory getDroppedInventory() {
        return upgradesInventory;
    }

    public InventorySimple getUpgradesInventory() {
        return upgradesInventory;
    }

    public IInventory getInventory() {
        return inventory;
    }
}

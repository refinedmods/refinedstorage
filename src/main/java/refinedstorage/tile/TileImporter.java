package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.container.ContainerImporter;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.UpgradeItemHandler;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.ModeConstants;
import refinedstorage.tile.config.ModeFilter;

public class TileImporter extends TileNode implements ICompareConfig, IModeConfig {
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private BasicItemHandler filters = new BasicItemHandler(9, this);
    private UpgradeItemHandler upgrades = new UpgradeItemHandler(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    private int currentSlot;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.importerUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        IItemHandler handler = TileBase.getItemHandler(getFacingTile(), getDirection().getOpposite());

        if (getFacingTile() instanceof TileDiskDrive || handler == null) {
            return;
        }

        if (currentSlot >= handler.getSlots()) {
            currentSlot = 0;
        }

        if (handler.getSlots() > 0) {
            ItemStack stack = handler.getStackInSlot(currentSlot);

            if (stack == null || !ModeFilter.respectsMode(filters, this, compare, stack)) {
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
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        TileBase.readItems(filters, 0, nbt);
        TileBase.readItems(upgrades, 1, nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        TileBase.writeItems(filters, 0, tag);
        TileBase.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerImporter.class;
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

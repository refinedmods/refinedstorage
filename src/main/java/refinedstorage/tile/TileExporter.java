package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.apiimpl.autocrafting.CraftingTaskScheduler;
import refinedstorage.container.ContainerExporter;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileExporter extends TileNode implements ICompareConfig {
    private static final String NBT_COMPARE = "Compare";

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = 0;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler(this);

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.exporterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        IItemHandler handler = getItemHandler(getFacingTile(), getDirection().getOpposite());

        int size = upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1;

        if (handler != null && ticks % upgrades.getSpeed() == 0) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null) {
                    ItemStack took = network.extractItem(slot, size, compare);

                    if (took != null) {
                        scheduler.resetSchedule();

                        ItemStack remainder = ItemHandlerHelper.insertItem(handler, took, false);

                        if (remainder != null) {
                            network.insertItem(remainder, remainder.stackSize, false);
                        }
                    } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                        if (scheduler.canSchedule(compare, slot)) {
                            scheduler.schedule(network, compare, slot);
                        }
                    }
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        readItems(filters, 0, tag);
        readItems(upgrades, 1, tag);

        scheduler.read(tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);

        writeItems(filters, 0, tag);
        writeItems(upgrades, 1, tag);

        scheduler.writeToNBT(tag);

        return tag;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerExporter.class;
    }

    public IItemHandler getFilters() {
        return filters;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
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

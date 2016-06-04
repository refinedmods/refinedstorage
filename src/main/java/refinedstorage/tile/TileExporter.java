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
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.autocrafting.CraftingTaskScheduler;
import refinedstorage.container.ContainerExporter;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileExporter extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    private BasicItemHandler filters = new BasicItemHandler(9, this);
    private BasicItemHandler upgrades = new BasicItemHandler(
        4,
        this,
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED),
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING),
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_STACK)
    );

    private int compare = 0;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler();

    @Override
    public int getEnergyUsage() {
        return 2 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
        IItemHandler handler = RefinedStorageUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

        if (handler != null && ticks % RefinedStorageUtils.getSpeed(upgrades) == 0) {
            for (int i = 0; i < filters.getSlots(); ++i) {
                ItemStack slot = filters.getStackInSlot(i);

                if (slot != null) {
                    int size = RefinedStorageUtils.hasUpgrade(upgrades, ItemUpgrade.TYPE_STACK) ? 64 : 1;

                    ItemStack took = controller.take(slot, size, compare);

                    if (took != null) {
                        scheduler.resetSchedule();

                        if (ItemHandlerHelper.insertItem(handler, took, true) == null) {
                            ItemHandlerHelper.insertItem(handler, took, false);

                            return;
                        }

                        controller.push(took);
                    } else if (RefinedStorageUtils.hasUpgrade(upgrades, ItemUpgrade.TYPE_CRAFTING)) {
                        if (scheduler.canSchedule(compare, slot)) {
                            scheduler.schedule(controller, compare, slot);
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
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        RefinedStorageUtils.readItems(filters, 0, nbt);
        RefinedStorageUtils.readItems(upgrades, 1, nbt);

        scheduler.read(nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);

        RefinedStorageUtils.writeItems(filters, 0, tag);
        RefinedStorageUtils.writeItems(upgrades, 1, tag);

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

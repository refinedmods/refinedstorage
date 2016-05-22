package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
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
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING)
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
                    ItemStack took = controller.take(ItemHandlerHelper.copyStackWithSize(slot, 1), compare);

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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        RefinedStorageUtils.restoreItems(filters, 0, nbt);
        RefinedStorageUtils.restoreItems(upgrades, 1, nbt);

        scheduler.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);

        RefinedStorageUtils.saveItems(filters, 0, nbt);
        RefinedStorageUtils.saveItems(upgrades, 1, nbt);

        scheduler.writeToNBT(nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

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
}

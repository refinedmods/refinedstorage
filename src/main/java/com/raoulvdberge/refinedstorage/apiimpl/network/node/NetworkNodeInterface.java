package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemItemHandler;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerProxy;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class NetworkNodeInterface extends NetworkNode implements IComparable {
    public static final String ID = "interface";

    private static final String NBT_COMPARE = "Compare";
    private static final int NUM_IMPORT_SLOTS = 9;
    private static final int NUM_EXPORT_SLOTS = 9;
    private static final int[] NUM_ITEMS_UPGRADE = new int[]{1, 2, 4, 8, 16};

    private ItemHandlerBase importItems = new ItemHandlerBase(NUM_IMPORT_SLOTS, new ItemHandlerListenerNetworkNode(this));

    private ItemHandlerBase exportFilterItems = new ItemHandlerBase(NUM_EXPORT_SLOTS, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerBase exportItems = new ItemHandlerBase(NUM_EXPORT_SLOTS, new ItemHandlerListenerNetworkNode(this));

    private IItemHandler items = new ItemHandlerProxy(importItems, exportItems);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK, ItemUpgrade.TYPE_CRAFTING);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;

    private int currentSlot = 0;

    public NetworkNodeInterface(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.interfaceUsage + upgrades.getEnergyUsage();
    }

    private int importFromCurrentSlot(int maxSize) {
        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot.isEmpty()) {
            return 0;
        }

        int size = Math.min(slot.getCount(), maxSize);
        ItemStack remainder = network.insertItemTracked(slot, size);
        int left = (remainder == null) ? 0 : remainder.getCount();
        importItems.extractItem(currentSlot, size - left, false);

        return size - left;
    }

    @Override
    public void update() {
        super.update();

        if (network == null || !canUpdate()) {
            return;
        }

        int startSlot = currentSlot;

        if (upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK)) {
            // import entire stacks at once
            // 0/1/2/3 speed upgrades = 1/4/7/9 stacks
            int maxStacks = Math.min(upgrades.getSpeed(1, -3), 9);

            // extract `maxStacks` stacks or until we've reached the starting slot
            int stacks = 0;
            do {
                if (importFromCurrentSlot(Integer.MAX_VALUE) > 0) {
                    stacks++;
                }
                currentSlot = (currentSlot + 1) % NUM_IMPORT_SLOTS;
            } while (currentSlot != startSlot && stacks < maxStacks);
        } else {
            // import up to `numItems` items total
            int numItems = NUM_ITEMS_UPGRADE[upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED)];

            do {
                numItems -= importFromCurrentSlot(numItems);
                if (numItems == 0) {
                  break;
                }
                currentSlot = (currentSlot + 1) % NUM_IMPORT_SLOTS;
            } while (currentSlot != startSlot);
        }

        for (int i = 0; i < NUM_EXPORT_SLOTS; ++i) {
            ItemStack wanted = exportFilterItems.getStackInSlot(i);
            ItemStack got = exportItems.getStackInSlot(i);

            if (wanted.isEmpty()) {
                if (!got.isEmpty()) {
                    exportItems.setStackInSlot(i, StackUtils.nullToEmpty(network.insertItemTracked(got, got.getCount())));
                }
            } else if (!got.isEmpty() && !API.instance().getComparer().isEqual(wanted, got, getCompare())) {
                exportItems.setStackInSlot(i, StackUtils.nullToEmpty(network.insertItemTracked(got, got.getCount())));
            } else {
                int delta = got.isEmpty() ? wanted.getCount() : (wanted.getCount() - got.getCount());

                if (delta > 0) {
                    ItemStack result = network.extractItem(wanted, delta, compare, false, s -> !(s instanceof StorageItemItemHandler) || !((StorageItemItemHandler) s).isConnectedToInterface());

                    if (result != null) {
                        if (exportItems.getStackInSlot(i).isEmpty()) {
                            exportItems.setStackInSlot(i, result);
                        } else {
                            exportItems.getStackInSlot(i).grow(result.getCount());
                        }
                    }

                    // Example: our delta is 5, we extracted 3 items.
                    // That means we still have to autocraft 2 items.
                    delta -= result == null ? 0 : result.getCount();

                    if (delta > 0 && upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                        network.getCraftingManager().schedule(wanted, delta);
                    }
                } else if (delta < 0) {
                    ItemStack remainder = network.insertItemTracked(got, Math.abs(delta));

                    if (remainder == null) {
                        exportItems.extractItem(i, Math.abs(delta), false);
                    } else {
                        exportItems.extractItem(i, Math.abs(delta) - remainder.getCount(), false);
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

        StackUtils.readItems(importItems, 0, tag);
        StackUtils.readItems(exportItems, 2, tag);
        StackUtils.readItems(upgrades, 3, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(importItems, 0, tag);
        StackUtils.writeItems(exportItems, 2, tag);
        StackUtils.writeItems(upgrades, 3, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(exportFilterItems, 1, tag);

        tag.setInteger(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(exportFilterItems, 1, tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }
    }

    public IItemHandler getImportItems() {
        return importItems;
    }

    public IItemHandler getExportFilterItems() {
        return exportFilterItems;
    }

    public IItemHandler getExportItems() {
        return exportItems;
    }

    public IItemHandler getItems() {
        return items;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(importItems, exportItems, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

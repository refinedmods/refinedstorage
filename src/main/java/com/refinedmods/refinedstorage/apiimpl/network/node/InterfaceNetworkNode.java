package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.ItemExternalStorage;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ProxyItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class InterfaceNetworkNode extends NetworkNode implements IComparable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "interface");

    private static final String NBT_COMPARE = "Compare";

    private final BaseItemHandler importItems = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));

    private final BaseItemHandler exportFilterItems = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private final BaseItemHandler exportItems = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));

    private final IItemHandler items = new ProxyItemHandler(importItems, exportItems);

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK, UpgradeItem.Type.CRAFTING)
        .addListener(new NetworkNodeInventoryListener(this));

    private int compare = IComparer.COMPARE_NBT;

    private int currentSlot = 0;

    public InterfaceNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getInterface().getUsage() + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate()) {
            return;
        }

        if (currentSlot >= importItems.getSlots()) {
            currentSlot = 0;
        }

        ItemStack slot = importItems.getStackInSlot(currentSlot);

        if (slot.isEmpty()) {
            currentSlot++;
        } else if (ticks % upgrades.getSpeed() == 0) {
            int size = Math.min(slot.getCount(), upgrades.getStackInteractCount());

            ItemStack remainder = network.insertItemTracked(slot, size);

            importItems.extractItem(currentSlot, size - remainder.getCount(), false);
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack wanted = exportFilterItems.getStackInSlot(i);
            ItemStack got = exportItems.getStackInSlot(i);

            if (wanted.isEmpty()) {
                if (!got.isEmpty()) {
                    exportItems.setStackInSlot(i, network.insertItemTracked(got, got.getCount()));
                }
            } else if (!got.isEmpty() && !API.instance().getComparer().isEqual(wanted, got, getCompare())) {
                exportItems.setStackInSlot(i, network.insertItemTracked(got, got.getCount()));
            } else {
                int delta = got.isEmpty() ? wanted.getCount() : (wanted.getCount() - got.getCount());

                if (delta > 0) {
                    final boolean actingAsStorage = isActingAsStorage();

                    ItemStack result = network.extractItem(wanted, delta, compare, Action.PERFORM, s -> {
                        // If we are not an interface acting as a storage, we can extract from anywhere.
                        if (!actingAsStorage) {
                            return true;
                        }

                        // If we are an interface acting as a storage, we don't want to extract from other interfaces to
                        // avoid stealing from each other.
                        return !(s instanceof ItemExternalStorage) || !((ItemExternalStorage) s).isConnectedToInterface();
                    });

                    if (!result.isEmpty()) {
                        if (exportItems.getStackInSlot(i).isEmpty()) {
                            exportItems.setStackInSlot(i, result);
                        } else {
                            exportItems.getStackInSlot(i).grow(result.getCount());
                        }
                    }

                    // Example: our delta is 5, we extracted 3 items.
                    // That means we still have to autocraft 2 items.
                    delta -= result.getCount();

                    if (delta > 0 && upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                        network.getCraftingManager().request(new SlottedCraftingRequest(this, i), wanted, delta);
                    }
                } else if (delta < 0) {
                    ItemStack remainder = network.insertItemTracked(got, Math.abs(delta));

                    exportItems.extractItem(i, Math.abs(delta) - remainder.getCount(), false);
                }
            }
        }
    }

    private boolean isActingAsStorage() {
        for (Direction facing : Direction.values()) {
            INetworkNode facingNode = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(pos.relative(facing));

            if (facingNode instanceof ExternalStorageNetworkNode &&
                facingNode.isActive() &&
                ((ExternalStorageNetworkNode) facingNode).getDirection() == facing.getOpposite() &&
                ((ExternalStorageNetworkNode) facingNode).getType() == IType.ITEMS) {
                return true;
            }
        }

        return false;
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
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(importItems, 0, tag);
        StackUtils.readItems(exportItems, 2, tag);
        StackUtils.readItems(upgrades, 3, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(importItems, 0, tag);
        StackUtils.writeItems(exportItems, 2, tag);
        StackUtils.writeItems(upgrades, 3, tag);

        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(exportFilterItems, 1, tag);

        tag.putInt(NBT_COMPARE, compare);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(exportFilterItems, 1, tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
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
}

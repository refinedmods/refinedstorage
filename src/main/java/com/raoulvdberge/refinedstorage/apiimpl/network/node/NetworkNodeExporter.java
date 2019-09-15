package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

public class NetworkNodeExporter extends NetworkNode implements IComparable, IType, ICoverable {
    public static final String ID = "exporter";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_COVERS = "Covers";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this)/* TODO, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK*/);

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;

    private CoverManager coverManager = new CoverManager(this);

    private int filterSlot;

    public NetworkNodeExporter(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.exporterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed() == 0) {
            if (type == IType.ITEMS) {
                IItemHandler handler = WorldUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

                if (handler != null) {
                    while (filterSlot + 1 < itemFilters.getSlots() && itemFilters.getStackInSlot(filterSlot).isEmpty()) {
                        filterSlot++;
                    }

                    // We jump out of the loop above if we reach the maximum slot. If the maximum slot is empty,
                    // we waste a tick with doing nothing because it's empty. Hence this check. If we are at the last slot
                    // and it's empty, go back to slot 0.
                    // We also handle if we exceeded the maximum slot in general.
                    if ((filterSlot == itemFilters.getSlots() - 1 && itemFilters.getStackInSlot(filterSlot).isEmpty()) || (filterSlot >= itemFilters.getSlots())) {
                        filterSlot = 0;
                    }

                    ItemStack slot = itemFilters.getStackInSlot(filterSlot);

                    if (!slot.isEmpty()) {
                        int stackSize = upgrades.getItemInteractCount();

                        ItemStack took = network.extractItem(slot, Math.min(slot.getMaxStackSize(), stackSize), compare, Action.SIMULATE);

                        if (took == null) {
                            if (upgrades.hasUpgrade(ItemUpgrade.Type.CRAFTING)) {
                                network.getCraftingManager().request(new SlottedCraftingRequest(this, filterSlot), slot, stackSize);
                            }
                        } else if (ItemHandlerHelper.insertItem(handler, took, true).isEmpty()) {
                            took = network.extractItem(slot, Math.min(slot.getMaxStackSize(), stackSize), compare, Action.PERFORM);

                            if (took != null) {
                                ItemHandlerHelper.insertItem(handler, took, false);
                            }
                        }
                    }

                    filterSlot++;
                }
            } else if (type == IType.FLUIDS) {
                FluidStack[] fluids = fluidFilters.getFluids();

                while (filterSlot + 1 < fluids.length && fluids[filterSlot] == null) {
                    filterSlot++;
                }

                // We jump out of the loop above if we reach the maximum slot. If the maximum slot is empty,
                // we waste a tick with doing nothing because it's empty. Hence this check. If we are at the last slot
                // and it's empty, go back to slot 0.
                // We also handle if we exceeded the maximum slot in general.
                if ((filterSlot == fluids.length - 1 && fluids[filterSlot] == null) || (filterSlot >= fluids.length)) {
                    filterSlot = 0;
                }

                IFluidHandler handler = WorldUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());

                if (handler != null) {
                    FluidStack stack = fluids[filterSlot];

                    if (stack != null) {
                        int toExtract = FluidAttributes.BUCKET_VOLUME * upgrades.getItemInteractCount();

                        FluidStack stackInStorage = network.getFluidStorageCache().getList().get(stack, compare);

                        if (stackInStorage != null) {
                            toExtract = Math.min(toExtract, stackInStorage.getAmount());

                            FluidStack took = network.extractFluid(stack, toExtract, compare, Action.SIMULATE);

                            if (took != null) {
                                int filled = handler.fill(took, IFluidHandler.FluidAction.SIMULATE);

                                if (filled > 0) {
                                    took = network.extractFluid(stack, filled, compare, Action.PERFORM);

                                    handler.fill(took, IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        } else if (upgrades.hasUpgrade(ItemUpgrade.Type.CRAFTING)) {
                            network.getCraftingManager().request(this, stack, toExtract);
                        }
                    }

                    filterSlot++;
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
    public String getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        tag.put(NBT_COVERS, coverManager.writeToNbt());

        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 1, tag);

        if (tag.contains(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(upgrades, coverManager.getAsInventory());
    }

    @Override
    public int getType() {
        return world.isRemote ? TileExporter.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public boolean canConduct(@Nullable Direction direction) {
        return coverManager.canConduct(direction);
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}

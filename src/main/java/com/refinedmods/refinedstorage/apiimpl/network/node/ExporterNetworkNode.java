package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.ExporterTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ExporterNetworkNode extends NetworkNode implements IComparable, IType, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "exporter");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private final BaseItemHandler itemFilters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));
    private final CoverManager coverManager;
    private int compare = IComparer.COMPARE_NBT;    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.CRAFTING, UpgradeItem.Type.STACK, UpgradeItem.Type.REGULATOR)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading && !getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                boolean changed = false;

                for (int i = 0; i < itemFilters.getSlots(); ++i) {
                    ItemStack filteredItem = itemFilters.getStackInSlot(i);

                    if (filteredItem.getCount() > 1) {
                        filteredItem.setCount(1);
                        changed = true;
                    }
                }

                for (int i = 0; i < fluidFilters.getSlots(); ++i) {
                    FluidStack filteredFluid = fluidFilters.getFluid(i);

                    if (!filteredFluid.isEmpty() && filteredFluid.getAmount() != FluidAttributes.BUCKET_VOLUME) {
                        filteredFluid.setAmount(FluidAttributes.BUCKET_VOLUME);
                        changed = true;
                    }
                }

                if (changed) {
                    markDirty();
                }
            }
        });
    private int type = IType.ITEMS;
    private int filterSlot;

    public ExporterNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
        this.coverManager = new CoverManager(this);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getExporter().getUsage() + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed() == 0 && level.isLoaded(pos)) {
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
                        int stackSize = upgrades.getStackInteractCount();

                        if (upgrades.hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                            int found = 0;

                            for (int i = 0; i < handler.getSlots(); i++) {
                                ItemStack stackInConnectedHandler = handler.getStackInSlot(i);

                                if (API.instance().getComparer().isEqual(slot, stackInConnectedHandler, compare)) {
                                    found += stackInConnectedHandler.getCount();
                                }
                            }

                            int needed = 0;

                            for (int i = 0; i < itemFilters.getSlots(); ++i) {
                                if (API.instance().getComparer().isEqualNoQuantity(slot, itemFilters.getStackInSlot(i))) {
                                    needed += itemFilters.getStackInSlot(i).getCount();
                                }
                            }

                            stackSize = Math.min(stackSize, needed - found);
                        }

                        if (stackSize > 0) {
                            ItemStack took = network.extractItem(slot, Math.min(slot.getMaxStackSize(), stackSize), compare, Action.SIMULATE);

                            if (took.isEmpty()) {
                                if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                                    network.getCraftingManager().request(new SlottedCraftingRequest(this, filterSlot), slot, stackSize);
                                }
                            } else {
                                ItemStack remainder = ItemHandlerHelper.insertItem(handler, took, true);

                                int correctedStackSize = took.getCount() - remainder.getCount();

                                if (correctedStackSize > 0) {
                                    took = network.extractItem(slot, correctedStackSize, compare, Action.PERFORM);

                                    ItemHandlerHelper.insertItem(handler, took, false);
                                }
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
                        int toExtract = FluidAttributes.BUCKET_VOLUME * upgrades.getStackInteractCount();

                        FluidStack stackInStorage = network.getFluidStorageCache().getList().get(stack, compare);

                        if (stackInStorage != null) {
                            toExtract = Math.min(toExtract, stackInStorage.getAmount());

                            if (upgrades.hasUpgrade(UpgradeItem.Type.REGULATOR)) {
                                int found = 0;

                                for (int i = 0; i < handler.getTanks(); i++) {
                                    FluidStack stackInConnectedHandler = handler.getFluidInTank(i);

                                    if (API.instance().getComparer().isEqual(stack, stackInConnectedHandler, compare)) {
                                        found += stackInConnectedHandler.getAmount();
                                    }
                                }

                                int needed = 0;

                                for (int i = 0; i < fluidFilters.getSlots(); ++i) {
                                    if (API.instance().getComparer().isEqual(stack, fluidFilters.getFluid(i), IComparer.COMPARE_NBT)) {
                                        needed += fluidFilters.getFluid(i).getAmount();
                                    }
                                }

                                toExtract = Math.min(toExtract, needed - found);
                            }

                            if (toExtract > 0) {
                                FluidStack took = network.extractFluid(stack, toExtract, compare, Action.SIMULATE);

                                int filled = handler.fill(took, IFluidHandler.FluidAction.SIMULATE);

                                if (filled > 0) {
                                    took = network.extractFluid(stack, filled, compare, Action.PERFORM);

                                    handler.fill(took, IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        } else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.coverManager.writeToNbt());

        StackUtils.writeItems(upgrades, 1, tag);
        return tag;
    }

    @Override
    public CompoundTag writeConfiguration(CompoundTag tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        if (tag.contains(CoverManager.NBT_COVER_MANAGER)) {
            this.coverManager.readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));
        }

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public void readConfiguration(CompoundTag tag) {
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

    public UpgradeItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return getUpgrades();
    }

    @Override
    public int getType() {
        return level.isClientSide ? ExporterTile.TYPE.getValue() : type;
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
    public CoverManager getCoverManager() {
        return coverManager;
    }




}

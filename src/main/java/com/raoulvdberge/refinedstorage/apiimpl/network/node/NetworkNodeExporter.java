package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemFilter;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class NetworkNodeExporter extends NetworkNode implements IComparable, IType {
    public static final String ID = "exporter";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_REGULATOR = "Regulator";

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private boolean regulator = false;

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

        if (network != null && canUpdate() && ticks % upgrades.getSpeed() == 0) {
            if (type == IType.ITEMS) {
                IItemHandler handler = WorldUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

                if (handler != null) {
                    for (int i = 0; i < itemFilters.getSlots(); ++i) {
                        ItemStack slot = itemFilters.getStackInSlot(i);

                        if (!slot.isEmpty()) {
                            if (slot.getItem() == RSItems.FILTER) {
                                for (ItemStack slotInFilter : ItemFilter.getFilterItemsFromCache(slot)) {
                                    if (!slotInFilter.isEmpty()) {
                                        doExport(handler, slotInFilter);
                                    }
                                }
                            } else {
                                doExport(handler, slot);
                            }
                        }
                    }
                }
            } else if (type == IType.FLUIDS) {
                IFluidHandler handler = WorldUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());

                if (handler != null) {
                    for (FluidStack stack : fluidFilters.getFluids()) {
                        if (stack != null) {
                            FluidStack stackInStorage = network.getFluidStorageCache().getList().get(stack, compare);

                            if (stackInStorage != null) {
                                int toExtract = Math.min(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), stackInStorage.amount);

                                boolean skipSlot = false;

                                if (regulator) {
                                    for (IFluidTankProperties tankProperty : handler.getTankProperties()) {
                                        FluidStack exporterStack = tankProperty.getContents();

                                        if (API.instance().getComparer().isEqual(stackInStorage, exporterStack, compare)) {
                                            if (exporterStack.amount >= stack.amount * Fluid.BUCKET_VOLUME) {
                                                skipSlot = true;

                                                break;
                                            } else {
                                                toExtract = upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? stack.amount * Fluid.BUCKET_VOLUME - exporterStack.amount : Fluid.BUCKET_VOLUME;
                                                toExtract = Math.min(toExtract, stackInStorage.amount);
                                            }
                                        }
                                    }
                                }

                                if (skipSlot) {
                                    continue;
                                }

                                FluidStack took = network.extractFluid(stack, toExtract, compare, true);

                                if (took != null) {
                                    int filled = handler.fill(took, false);

                                    if (filled > 0) {
                                        took = network.extractFluid(stack, filled, compare, false);

                                        handler.fill(took, true);

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void doExport(IItemHandler handler, ItemStack slot) {
        int stackSize = upgrades.getItemInteractCount();

        if (regulator) {
            int found = 0;

            for (int index = 0; index < handler.getSlots(); index++) {
                ItemStack handlerStack = handler.getStackInSlot(index);

                if (API.instance().getComparer().isEqual(slot, handlerStack, compare)) {
                    found += handlerStack.getCount();
                }
            }

            stackSize = slot.getCount() - found;

            if (stackSize <= 0) {
                return;
            } else if (!upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK)) {
                stackSize = 1;
            }
        }

        ItemStack took = network.extractItem(slot, Math.min(slot.getMaxStackSize(), stackSize), compare, true);

        if (took == null) {
            if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                network.getCraftingManager().schedule(slot, 1, compare);
            }
        } else if (ItemHandlerHelper.insertItem(handler, took, true).isEmpty()) {
            took = network.extractItem(slot, Math.min(slot.getMaxStackSize(), stackSize), compare, false);

            if (took != null) {
                ItemHandlerHelper.insertItem(handler, took, false);
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

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);
        tag.setBoolean(NBT_REGULATOR, regulator);

        StackUtils.writeItems(itemFilters, 0, tag);
        StackUtils.writeItems(fluidFilters, 2, tag);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        if (tag.hasKey(NBT_REGULATOR)) {
            regulator = tag.getBoolean(NBT_REGULATOR);
        }

        StackUtils.readItems(itemFilters, 0, tag);
        StackUtils.readItems(fluidFilters, 2, tag);
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
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

    public void setRegulator(boolean regulator) {
        this.regulator = regulator;
    }

    public boolean isRegulator() {
        return world.isRemote ? TileExporter.REGULATOR.getValue() : regulator;
    }

    public ItemHandlerBase getItemFilters() {
        return itemFilters;
    }

    public ItemHandlerFluid getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }
}

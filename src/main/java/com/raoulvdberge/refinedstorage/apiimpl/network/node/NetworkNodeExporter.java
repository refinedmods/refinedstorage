package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerChangeListenerNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class NetworkNodeExporter extends NetworkNode implements IComparable, IType {
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_REGULATOR = "Regulator";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, new ItemHandlerChangeListenerNode(this));
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, new ItemHandlerChangeListenerNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerChangeListenerNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private boolean regulator = false;

    public NetworkNodeExporter(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.exporterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        if (network != null && ticks % upgrades.getSpeed() == 0) {
            if (type == IType.ITEMS) {
                IItemHandler handler = RSUtils.getItemHandler(holder.world().getTileEntity(holder.pos().offset(holder.getDirection())), holder.getDirection().getOpposite());

                if (handler != null) {
                    for (int i = 0; i < itemFilters.getSlots(); ++i) {
                        ItemStack slot = itemFilters.getStackInSlot(i);

                        if (!slot.isEmpty()) {
                            int stackSize = upgrades.getItemInteractCount();

                            boolean skipSlot = false;

                            if (regulator) {
                                for (int index = 0; i < handler.getSlots() && !skipSlot; i++) {
                                    ItemStack exporterStack = handler.getStackInSlot(index);

                                    if (API.instance().getComparer().isEqual(slot, exporterStack, compare)) {
                                        if (exporterStack.getCount() >= slot.getCount()) {
                                            skipSlot = true;
                                        } else {
                                            stackSize = upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? slot.getCount() - exporterStack.getCount() : 1;
                                        }
                                    }
                                }
                            }

                            if (skipSlot) {
                                continue;
                            }

                            ItemStack took = network.extractItem(slot, stackSize, compare, true);

                            if (took == null) {
                                if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                                    network.scheduleCraftingTask(slot, 1, compare);
                                }
                            } else if (ItemHandlerHelper.insertItem(handler, took, true).isEmpty()) {
                                took = network.extractItem(slot, upgrades.getItemInteractCount(), compare, false);

                                ItemHandlerHelper.insertItem(handler, took, false);
                            }
                        }
                    }
                }
            } else if (type == IType.FLUIDS) {
                IFluidHandler handler = RSUtils.getFluidHandler(holder.world().getTileEntity(holder.pos().offset(holder.getDirection())), holder.getDirection().getOpposite());

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

        RSUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);
        tag.setBoolean(NBT_REGULATOR, regulator);

        RSUtils.writeItems(itemFilters, 0, tag);
        RSUtils.writeItems(fluidFilters, 2, tag);

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

        RSUtils.readItems(itemFilters, 0, tag);
        RSUtils.readItems(fluidFilters, 2, tag);
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
        return holder.world().isRemote ? TileExporter.TYPE.getValue() : type;
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
        return holder.world().isRemote ? TileExporter.REGULATOR.getValue() : regulator;
    }

    public ItemHandlerBasic getItemFilters() {
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

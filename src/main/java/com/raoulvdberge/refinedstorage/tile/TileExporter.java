package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerExporter;
import com.raoulvdberge.refinedstorage.gui.GuiExporter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataListener;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class TileExporter extends TileNode implements IComparable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> REGULATOR = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileExporter>() {
        @Override
        public Boolean getValue(TileExporter tile) {
            return tile.regulator;
        }
    }, new ITileDataConsumer<Boolean, TileExporter>() {
        @Override
        public void setValue(TileExporter tile, Boolean value) {
            if (!value && tile.regulator) {
                for (int i = 0; i < tile.itemFilters.getSlots() + tile.fluidFilters.getSlots(); ++i) {
                    ItemStack slot = i >= tile.itemFilters.getSlots() ? tile.fluidFilters.getStackInSlot(i - tile.itemFilters.getSlots()) : tile.itemFilters.getStackInSlot(i);

                    if (!slot.isEmpty()) {
                        slot.setCount(1);
                    }
                }
            }

            tile.regulator = value;

            tile.markDirty();

            tile.getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.openContainer instanceof ContainerExporter && ((ContainerExporter) player.openContainer).getTile().getPos().equals(tile.getPos()))
                .forEach(player -> {
                    ((ContainerExporter) player.openContainer).initSlots();

                    player.openContainer.detectAndSendChanges();
                });
        }
    }, new ITileDataListener<Boolean>() {
        @Override
        public void onChanged(TileDataParameter<Boolean> parameter) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiExporter) {
                ((ContainerExporter) ((GuiExporter) Minecraft.getMinecraft().currentScreen).inventorySlots).initSlots();
            }
        }
    });

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_REGULATOR = "Regulator";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private boolean regulator = false;

    public TileExporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(REGULATOR);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.exporterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed() == 0) {
            if (type == IType.ITEMS) {
                IItemHandler handler = RSUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());

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
                IFluidHandler handler = RSUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());

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
        return getWorld().isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    public boolean isRegulator() {
        return !getWorld().isRemote ? regulator : REGULATOR.getValue();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(upgrades);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

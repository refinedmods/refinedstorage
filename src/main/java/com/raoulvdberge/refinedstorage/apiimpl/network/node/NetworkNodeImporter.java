package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

public class NetworkNodeImporter extends NetworkNode implements IComparable, IFilterable, IType, ICoverable {
    public static final String ID = "importer";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_COVERS = "Covers";
    private static final String NBT_FLUID_FILTERS = "FLuidFilters";

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;
    private int type = IType.ITEMS;

    private CoverManager coverManager = new CoverManager(this);

    private int currentSlot;

    public NetworkNodeImporter(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.importerUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate()) {
            return;
        }

        if (type == IType.ITEMS) {
            TileEntity facing = getFacingTile();
            IItemHandler handler = WorldUtils.getItemHandler(facing, getDirection().getOpposite());

            if (facing instanceof TileDiskDrive || handler == null) {
                return;
            }

            if (currentSlot >= handler.getSlots()) {
                currentSlot = 0;
            }

            if (handler.getSlots() > 0) {
                while (currentSlot + 1 < handler.getSlots() && handler.getStackInSlot(currentSlot).isEmpty()) {
                    currentSlot++;
                }

                ItemStack stack = handler.getStackInSlot(currentSlot);

                if (!IFilterable.acceptsItem(itemFilters, mode, compare, stack)) {
                    currentSlot++;
                } else if (ticks % upgrades.getSpeed() == 0) {
                    ItemStack result = handler.extractItem(currentSlot, upgrades.getItemInteractCount(), true);

                    if (!result.isEmpty() && network.insertItem(result, result.getCount(), Action.SIMULATE) == null) {
                        result = handler.extractItem(currentSlot, upgrades.getItemInteractCount(), false);

                        if (!result.isEmpty()) {
                            network.insertItemTracked(result, result.getCount());
                        }
                    } else {
                        currentSlot++;
                    }
                }
            }
        } else if (type == IType.FLUIDS && ticks % upgrades.getSpeed() == 0) {
            IFluidHandler handler = WorldUtils.getFluidHandler(getFacingTile(), getDirection().getOpposite());

            if (handler != null) {
                FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                if (stack != null && IFilterable.acceptsFluid(fluidFilters, mode, compare, stack) && network.insertFluid(stack, stack.amount, Action.SIMULATE) == null) {
                    FluidStack toDrain = handler.drain(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), false);

                    if (toDrain != null) {
                        FluidStack remainder = network.insertFluidTracked(toDrain, toDrain.amount);
                        if (remainder != null) {
                            toDrain.amount -= remainder.amount;
                        }

                        handler.drain(toDrain, true);
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
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 1, tag);

        if (tag.hasKey(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getTagList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        tag.setTag(NBT_COVERS, coverManager.writeToNbt());

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
        }

        OneSixMigrationHelper.migrateEmptyWhitelistToEmptyBlacklist(version, this, itemFilters);
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(upgrades, coverManager.getAsInventory());
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        return coverManager.canConduct(direction);
    }

    @Override
    public int getType() {
        return world.isRemote ? TileImporter.TYPE.getValue() : type;
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

package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RS;
import refinedstorage.RSUtils;
import refinedstorage.api.util.IComparer;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

import java.util.ArrayList;
import java.util.List;

public class TileDestructor extends TileMultipartNode implements IComparable, IFilterable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> PICKUP = new TileDataParameter<Boolean>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileDestructor>() {
        @Override
        public Boolean getValue(TileDestructor tile) {
            return tile.pickupItem;
        }
    }, new ITileDataConsumer<Boolean, TileDestructor>() {
        @Override
        public void setValue(TileDestructor tile, Boolean value) {
            tile.pickupItem = value;

            tile.markDirty();
        }
    });

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_SILKTOUCH);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;

    public TileDestructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.destructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {

            BlockPos front = pos.offset(getDirection());

            if (pickupItem && type == IType.ITEMS) {
                List<Entity> droppedItems = new ArrayList<>();

                Chunk chunk = worldObj.getChunkFromBlockCoords(front);
                chunk.getEntitiesWithinAABBForEntity(null, new AxisAlignedBB(front), droppedItems, null);

                for (Entity entity : droppedItems) {
                    if (entity instanceof EntityItem) {
                        ItemStack droppedItem = ((EntityItem) entity).getEntityItem();

                        if (IFilterable.canTake(itemFilters, mode, compare, droppedItem) && network.insertItem(droppedItem, droppedItem.stackSize, true) == null) {
                            network.insertItem(droppedItem.copy(), droppedItem.stackSize, false);

                            worldObj.removeEntity(entity);

                            break;
                        }
                    }
                }
            } else if (type == IType.ITEMS) {
                IBlockState frontBlockState = worldObj.getBlockState(front);

                @SuppressWarnings("deprecation")
                ItemStack frontStack = frontBlockState.getBlock().getItem(worldObj, front, frontBlockState);

                if (frontStack != null) {
                    if (IFilterable.canTake(itemFilters, mode, compare, frontStack)) {
                        if(!upgrades.hasUpgrade(ItemUpgrade.TYPE_SILKTOUCH)) {
                            List<ItemStack> drops = frontBlockState.getBlock().getDrops(worldObj, front, frontBlockState, 0);

                            for (ItemStack drop : drops) {
                                if(network.insertItem(drop, drop.stackSize, true) != null) {
                                    return;
                                }
                            }

                            worldObj.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                            worldObj.setBlockToAir(front);

                            for (ItemStack drop : drops) {
                                // We check if the controller isn't null here because when a destructor faces a node and removes it
                                // it will essentially remove this block itself from the network without knowing
                                if (network == null) {
                                    InventoryHelper.spawnItemStack(worldObj, front.getX(), front.getY(), front.getZ(), drop);
                                } else {
                                    ItemStack remainder = network.insertItem(drop, drop.stackSize, false);

                                    if (remainder != null) {
                                        InventoryHelper.spawnItemStack(worldObj, front.getX(), front.getY(), front.getZ(), remainder);
                                    }
                                }
                            }
                        } else {
                            if(network.insertItem(frontStack.copy(), frontStack.stackSize, false) == null) {
                                worldObj.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                                worldObj.setBlockToAir(front);
                            }
                        }
                    }
                }
            } else if (type == IType.FLUIDS) {
                Block frontBlock = worldObj.getBlockState(front).getBlock();

                IFluidHandler handler = null;

                if (frontBlock instanceof BlockLiquid) {
                    handler = new BlockLiquidWrapper((BlockLiquid) frontBlock, worldObj, front);
                } else if (frontBlock instanceof IFluidBlock) {
                    handler = new FluidBlockWrapper((IFluidBlock) frontBlock, worldObj, front);
                }

                if (handler != null) {
                    FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                    if (stack != null && IFilterable.canTakeFluids(fluidFilters, mode, compare, stack) && network.insertFluid(stack, stack.amount, true) == null) {
                        FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME, true);

                        network.insertFluid(drained, drained.amount, false);
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

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        if (tag.hasKey(NBT_PICKUP)) {
            pickupItem = tag.getBoolean(NBT_PICKUP);
        }

        RSUtils.readItems(itemFilters, 0, tag);
        RSUtils.readItems(upgrades, 1, tag);
        RSUtils.readItems(fluidFilters, 2, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);
        tag.setBoolean(NBT_PICKUP, pickupItem);

        RSUtils.writeItems(itemFilters, 0, tag);
        RSUtils.writeItems(upgrades, 1, tag);
        RSUtils.writeItems(fluidFilters, 2, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IItemHandler getInventory() {
        return itemFilters;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public int getType() {
        return worldObj.isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
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

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.mojang.authlib.GameProfile;
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
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeDestructor extends NetworkNode implements IComparable, IFilterable, IType, ICoverable {
    public static final String ID = "destructor";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";
    private static final String NBT_COVERS = "Covers";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_SILK_TOUCH, ItemUpgrade.TYPE_FORTUNE_1, ItemUpgrade.TYPE_FORTUNE_2, ItemUpgrade.TYPE_FORTUNE_3);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;

    private CoverManager coverManager = new CoverManager(this);

    public NetworkNodeDestructor(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.destructorUsage + upgrades.getEnergyUsage();
    }

    private FakePlayer getFakePlayer() {
        WorldServer world = (WorldServer) this.world;
        UUID owner = getOwner();
        if (owner != null) {
            PlayerProfileCache profileCache = world.getMinecraftServer().getPlayerProfileCache();
            GameProfile profile = profileCache.getProfileByUUID(owner);
            if (profile != null) {
                return FakePlayerFactory.get(world, profile);
            }
        }
        return FakePlayerFactory.getMinecraft(world);
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            if (pickupItem && type == IType.ITEMS) {
                List<Entity> droppedItems = new ArrayList<>();

                Chunk chunk = world.getChunk(front);
                chunk.getEntitiesWithinAABBForEntity(null, new AxisAlignedBB(front), droppedItems, null);

                for (Entity entity : droppedItems) {
                    if (entity instanceof EntityItem) {
                        ItemStack droppedItem = ((EntityItem) entity).getItem();

                        if (IFilterable.acceptsItem(itemFilters, mode, compare, droppedItem) && network.insertItem(droppedItem, droppedItem.getCount(), Action.SIMULATE) == null) {
                            network.insertItemTracked(droppedItem.copy(), droppedItem.getCount());

                            world.removeEntity(entity);

                            break;
                        }
                    }
                }
            } else if (type == IType.ITEMS) {
                IBlockState frontBlockState = world.getBlockState(front);
                Block frontBlock = frontBlockState.getBlock();

                ItemStack frontStack = frontBlock.getPickBlock(
                    frontBlockState,
                    new RayTraceResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), getDirection().getOpposite()),
                    world,
                    front,
                    getFakePlayer()
                );

                if (!frontStack.isEmpty()) {
                    if (IFilterable.acceptsItem(itemFilters, mode, compare, frontStack) && frontBlockState.getBlockHardness(world, front) != -1.0) {
                        NonNullList<ItemStack> drops = NonNullList.create();

                        if (frontBlock instanceof BlockShulkerBox) {
                            drops.add(((BlockShulkerBox) frontBlock).getItem(world, front, frontBlockState));

                            TileEntity shulkerBoxTile = world.getTileEntity(front);

                            if (shulkerBoxTile instanceof TileEntityShulkerBox) {
                                // Avoid dropping the shulker box when Block#breakBlock is called
                                ((TileEntityShulkerBox) shulkerBoxTile).setDestroyedByCreativePlayer(true);
                                ((TileEntityShulkerBox) shulkerBoxTile).clear();
                            }
                        } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_SILK_TOUCH) && frontBlock.canSilkHarvest(world, front, frontBlockState, null)) {
                            drops.add(frontStack);
                        } else {
                            frontBlock.getDrops(drops, world, front, frontBlockState, upgrades.getFortuneLevel());
                        }

                        for (ItemStack drop : drops) {
                            if (network.insertItem(drop, drop.getCount(), Action.SIMULATE) != null) {
                                return;
                            }
                        }

                        BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, front, frontBlockState, getFakePlayer());

                        if (!MinecraftForge.EVENT_BUS.post(e)) {
                            world.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                            world.setBlockToAir(front);

                            for (ItemStack drop : drops) {
                                // We check if the controller isn't null here because when a destructor faces a node and removes it
                                // it will essentially remove this block itself from the network without knowing
                                if (network == null) {
                                    InventoryHelper.spawnItemStack(world, front.getX(), front.getY(), front.getZ(), drop);
                                } else {
                                    network.insertItemTracked(drop, drop.getCount());
                                }
                            }
                        }
                    }
                }
            } else if (type == IType.FLUIDS) {
                Block frontBlock = world.getBlockState(front).getBlock();

                IFluidHandler handler = null;

                if (frontBlock instanceof BlockLiquid) {
                    handler = new BlockLiquidWrapper((BlockLiquid) frontBlock, world, front);
                } else if (frontBlock instanceof IFluidBlock) {
                    handler = new FluidBlockWrapper((IFluidBlock) frontBlock, world, front);
                }

                if (handler != null) {
                    FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                    if (stack != null && IFilterable.acceptsFluid(fluidFilters, mode, compare, stack) && network.insertFluid(stack, stack.amount, Action.SIMULATE) == null) {
                        FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME, true);

                        network.insertFluidTracked(drained, drained.amount);
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
        tag.setBoolean(NBT_PICKUP, pickupItem);

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

        if (tag.hasKey(NBT_PICKUP)) {
            pickupItem = tag.getBoolean(NBT_PICKUP);
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

    public IItemHandler getInventory() {
        return itemFilters;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(upgrades, coverManager.getAsInventory());
    }

    @Override
    public int getType() {
        return world.isRemote ? TileDestructor.TYPE.getValue() : type;
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
    public boolean canConduct(@Nullable EnumFacing direction) {
        return coverManager.canConduct(direction);
    }

    public boolean isPickupItem() {
        return pickupItem;
    }

    public void setPickupItem(boolean pickupItem) {
        this.pickupItem = pickupItem;
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.mojang.authlib.GameProfile;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.UpgradeItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeDestructor extends NetworkNode implements IComparable, IWhitelistBlacklist, IType {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "destructor");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private BaseItemHandler itemFilters = new BaseItemHandler(9, new NetworkNodeListener(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new NetworkNodeListener(this));

    private UpgradeItemHandler upgrades = new UpgradeItemHandler(4, new NetworkNodeListener(this)/* TODO, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_SILK_TOUCH, ItemUpgrade.TYPE_FORTUNE_1, ItemUpgrade.TYPE_FORTUNE_2, ItemUpgrade.TYPE_FORTUNE_3*/);

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;

    public NetworkNodeDestructor(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.destructorUsage + upgrades.getEnergyUsage();
    }

    private FakePlayer getFakePlayer() {
        ServerWorld world = (ServerWorld) this.world;

        UUID owner = getOwner();

        if (owner != null) {
            PlayerProfileCache profileCache = world.getServer().getPlayerProfileCache();

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

                Chunk chunk = world.getChunkAt(front);
                chunk.getEntitiesWithinAABBForEntity(null, new AxisAlignedBB(front), droppedItems, null);

                for (Entity entity : droppedItems) {
                    if (entity instanceof ItemEntity) {
                        ItemStack droppedItem = ((ItemEntity) entity).getItem();

                        if (IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, droppedItem) && network.insertItem(droppedItem, droppedItem.getCount(), Action.SIMULATE) == null) {
                            network.insertItemTracked(droppedItem.copy(), droppedItem.getCount());

                            // TODO world.removeEntity(entity);

                            break;
                        }
                    }
                }
            } else if (type == IType.ITEMS) {
                BlockState frontBlockState = world.getBlockState(front);
                Block frontBlock = frontBlockState.getBlock();

                ItemStack frontStack = frontBlock.getPickBlock(
                    frontBlockState,
                    null,
                    // TODO    new BlockRayTraceResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), getDirection().getOpposite()),
                    world,
                    front,
                    getFakePlayer()
                );

                if (!frontStack.isEmpty()) {
                    if (IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, frontStack) && frontBlockState.getBlockHardness(world, front) != -1.0) {
                        NonNullList<ItemStack> drops = NonNullList.create();

                        /* TODO if (frontBlock instanceof ShulkerBoxTileEntity) {
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
                        }*/

                        for (ItemStack drop : drops) {
                            if (network.insertItem(drop, drop.getCount(), Action.SIMULATE) != null) {
                                return;
                            }
                        }

                        BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, front, frontBlockState, getFakePlayer());

                        if (!MinecraftForge.EVENT_BUS.post(e)) {
                            world.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                            world.removeBlock(front, false);

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
/* TODO
                if (frontBlock instanceof BlockLiquid) {
                    handler = new BlockLiquidWrapper((BlockLiquid) frontBlock, world, front);
                } else if (frontBlock instanceof IFluidBlock) {
                    handler = new FluidBlockWrapper((IFluidBlock) frontBlock, world, front);
                }

                if (handler != null) {
                    FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                    if (stack != null && IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack) && network.insertFluid(stack, stack.amount, Action.SIMULATE) == null) {
                        FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME, true);

                        network.insertFluidTracked(drained, drained.amount);
                    }
                }*/
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
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        tag.putBoolean(NBT_PICKUP, pickupItem);

        StackUtils.writeItems(itemFilters, 0, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        if (tag.contains(NBT_PICKUP)) {
            pickupItem = tag.getBoolean(NBT_PICKUP);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
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

    public boolean isPickupItem() {
        return pickupItem;
    }

    public void setPickupItem(boolean pickupItem) {
        this.pickupItem = pickupItem;
    }
}

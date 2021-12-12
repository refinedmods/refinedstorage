package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.DestructorTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;

public class DestructorNetworkNode extends NetworkNode implements IComparable, IWhitelistBlacklist, IType, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "destructor");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private final BaseItemHandler itemFilters = new BaseItemHandler(9).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(9).addListener(new NetworkNodeFluidInventoryListener(this));

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.SILK_TOUCH, UpgradeItem.Type.FORTUNE_1, UpgradeItem.Type.FORTUNE_2, UpgradeItem.Type.FORTUNE_3)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> tool = createTool());

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;
    private ItemStack tool = createTool();

    private final CoverManager coverManager;

    public DestructorNetworkNode(World world, BlockPos pos) {
        super(world, pos);
        this.coverManager = new CoverManager(this);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getDestructor().getUsage() + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0 && world.isLoaded(pos)) {
            if (type == IType.ITEMS) {
                if (pickupItem) {
                    pickupItems();
                } else {
                    breakBlock();
                }
            } else if (type == IType.FLUIDS) {
                breakFluid();
            }
        }
    }

    private void pickupItems() {
        BlockPos front = pos.relative(getDirection());

        List<Entity> droppedItems = new ArrayList<>();

        Chunk chunk = world.getChunkAt(front);
        chunk.getEntities((Entity)null, new AxisAlignedBB(front), droppedItems, null);

        for (Entity entity : droppedItems) {
            if (entity instanceof ItemEntity) {
                ItemStack droppedItem = ((ItemEntity) entity).getItem();

                if (IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, droppedItem) &&
                    network.insertItem(droppedItem, droppedItem.getCount(), Action.SIMULATE).isEmpty()) {
                    network.insertItemTracked(droppedItem.copy(), droppedItem.getCount());

                    entity.remove();

                    break;
                }
            }
        }
    }

    private void breakBlock() {
        BlockPos front = pos.relative(getDirection());
        BlockState frontBlockState = world.getBlockState(front);
        Block frontBlock = frontBlockState.getBlock();
        ItemStack frontStack = frontBlock.getPickBlock(
            frontBlockState,
            new BlockRayTraceResult(Vector3d.ZERO, getDirection().getOpposite(), front, false),
            world,
            front,
            WorldUtils.getFakePlayer((ServerWorld) world, getOwner())
        );

        if (!frontStack.isEmpty() &&
            IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, frontStack) &&
            frontBlockState.getDestroySpeed(world, front) != -1.0) {
            List<ItemStack> drops = Block.getDrops(
                frontBlockState,
                (ServerWorld) world,
                front,
                world.getBlockEntity(front),
                WorldUtils.getFakePlayer((ServerWorld) world, getOwner()),
                tool
            );

            for (ItemStack drop : drops) {
                if (!network.insertItem(drop, drop.getCount(), Action.SIMULATE).isEmpty()) {
                    return;
                }
            }

            BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, front, frontBlockState, WorldUtils.getFakePlayer((ServerWorld) world, getOwner()));

            if (!MinecraftForge.EVENT_BUS.post(e)) {
                frontBlock.playerWillDestroy(world, front, frontBlockState, WorldUtils.getFakePlayer((ServerWorld) world, getOwner()));

                world.removeBlock(front, false);

                for (ItemStack drop : drops) {
                    // We check if the controller isn't null here because when a destructor faces a node and removes it
                    // it will essentially remove this block itself from the network without knowing
                    if (network == null) {
                        InventoryHelper.dropItemStack(world, front.getX(), front.getY(), front.getZ(), drop);
                    } else {
                        network.insertItemTracked(drop, drop.getCount());
                    }
                }
            }
        }
    }

    private void breakFluid() {
        BlockPos front = pos.relative(getDirection());
        BlockState frontBlockState = world.getBlockState(front);
        Block frontBlock = frontBlockState.getBlock();

        if (frontBlock instanceof FlowingFluidBlock) {
            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
            if (frontBlockState.getValue(FlowingFluidBlock.LEVEL) == 0) {
                Fluid fluid = ((FlowingFluidBlock) frontBlock).getFluid();

                FluidStack stack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);

                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack) &&
                    network.insertFluid(stack, stack.getAmount(), Action.SIMULATE).isEmpty()) {
                    network.insertFluidTracked(stack, stack.getAmount());

                    world.setBlock(front, Blocks.AIR.defaultBlockState(), 11);
                }
            }
        } else if (frontBlock instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) frontBlock;

            if (fluidBlock.canDrain(world, front)) {
                FluidStack simulatedDrain = fluidBlock.drain(world, front, IFluidHandler.FluidAction.SIMULATE);

                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, simulatedDrain) &&
                    network.insertFluid(simulatedDrain, simulatedDrain.getAmount(), Action.SIMULATE).isEmpty()) {
                    FluidStack drained = fluidBlock.drain(world, front, IFluidHandler.FluidAction.EXECUTE);

                    network.insertFluidTracked(drained, drained.getAmount());
                }
            }
        }
    }

    private ItemStack createTool() {
        ItemStack newTool = new ItemStack(Items.DIAMOND_PICKAXE);

        if (upgrades.hasUpgrade(UpgradeItem.Type.SILK_TOUCH)) {
            newTool.enchant(Enchantments.SILK_TOUCH, 1);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_3)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_2)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 2);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_1)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 1);
        }

        return newTool;
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

        if (tag.contains(CoverManager.NBT_COVER_MANAGER)){
            this.coverManager.readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));
        }

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.coverManager.writeToNbt());

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

    @Override
    public IItemHandler getDrops() {
        return getUpgrades();
    }

    @Override
    public int getType() {
        return world.isClientSide ? DestructorTile.TYPE.getValue() : type;
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

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }
}

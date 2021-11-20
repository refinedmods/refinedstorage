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
import com.refinedmods.refinedstorage.tile.ConstructorTile;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConstructorNetworkNode extends NetworkNode implements IComparable, IType, ICoverable {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "constructor");

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_DROP = "Drop";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private final BaseItemHandler itemFilters = new BaseItemHandler(1).addListener(new NetworkNodeInventoryListener(this));
    private final FluidInventory fluidFilters = new FluidInventory(1)
        .addListener(new NetworkNodeFluidInventoryListener(this));

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.CRAFTING, UpgradeItem.Type.STACK)
        .addListener(new NetworkNodeInventoryListener(this));

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;
    private boolean drop = false;

    private final CoverManager coverManager;

    public ConstructorNetworkNode(World world, BlockPos pos) {
        super(world, pos);
        this.coverManager = new CoverManager(this);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getConstructor().getUsage() + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0 && world.isBlockPresent(pos)) {
            if (type == IType.ITEMS && !itemFilters.getStackInSlot(0).isEmpty()) {
                ItemStack stack = itemFilters.getStackInSlot(0);

                if (drop) {
                    extractAndDropItem(stack);
                } else if (stack.getItem() == Items.FIREWORK_ROCKET) {
                    extractAndSpawnFireworks(stack);
                } else if (stack.getItem() instanceof BlockItem) {
                    extractAndPlaceBlock(stack);
                }
            } else if (type == IType.FLUIDS && !fluidFilters.getFluid(0).isEmpty()) {
                extractAndPlaceFluid(fluidFilters.getFluid(0));
            }
        }
    }

    private void extractAndPlaceFluid(FluidStack stack) {
        BlockPos front = pos.offset(getDirection());

        if (network.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, compare, Action.SIMULATE).getAmount() < FluidAttributes.BUCKET_VOLUME) {
            if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                network.getCraftingManager().request(this, stack, FluidAttributes.BUCKET_VOLUME);
            }
        } else if (!world.getBlockState(front).getFluidState().isSource()) {
            FluidUtil.tryPlaceFluid(WorldUtils.getFakePlayer((ServerWorld) world, getOwner()), world, Hand.MAIN_HAND, front, new NetworkFluidHandler(StackUtils.copy(stack, FluidAttributes.BUCKET_VOLUME)), stack);
        }
    }

    private void extractAndPlaceBlock(ItemStack stack) {
        ItemStack took = network.extractItem(stack, 1, compare, Action.SIMULATE);
        if (!took.isEmpty()) {
            BlockItemUseContext ctx = new ConstructorBlockItemUseContext(
                world,
                WorldUtils.getFakePlayer((ServerWorld) world, getOwner()),
                Hand.MAIN_HAND,
                took,
                new BlockRayTraceResult(Vector3d.ZERO, getDirection(), pos, false)
            );

            ActionResultType result = ForgeHooks.onPlaceItemIntoWorld(ctx);
            if (result.isSuccessOrConsume()) {
                network.extractItem(stack, 1, Action.PERFORM);
            }
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
            ItemStack craft = itemFilters.getStackInSlot(0);

            network.getCraftingManager().request(this, craft, 1);
        }
    }

    private void extractAndDropItem(ItemStack stack) {
        ItemStack took = network.extractItem(stack, upgrades.getStackInteractCount(), compare, Action.PERFORM);

        if (!took.isEmpty()) {
            DefaultDispenseItemBehavior.doDispense(world, took, 6, getDirection(), new Position(getDispensePositionX(), getDispensePositionY(), getDispensePositionZ()));
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
            network.getCraftingManager().request(this, stack, 1);
        }
    }

    private void extractAndSpawnFireworks(ItemStack stack) {
        ItemStack took = network.extractItem(stack, 1, compare, Action.PERFORM);

        if (!took.isEmpty()) {
            world.addEntity(new FireworkRocketEntity(world, getDispensePositionX(), getDispensePositionY(), getDispensePositionZ(), took));
        }
    }

    private double getDispensePositionX() {
        return (double) pos.getX() + 0.5D + 0.8D * (double) getDirection().getXOffset();
    }

    private double getDispensePositionY() {
        return (double) pos.getY() + (getDirection() == Direction.DOWN ? 0.45D : 0.5D) + 0.8D * (double) getDirection().getYOffset();
    }

    private double getDispensePositionZ() {
        return (double) pos.getZ() + 0.5D + 0.8D * (double) getDirection().getZOffset();
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
        tag.putInt(NBT_TYPE, type);
        tag.putBoolean(NBT_DROP, drop);

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

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        if (tag.contains(NBT_DROP)) {
            drop = tag.getBoolean(NBT_DROP);
        }

        StackUtils.readItems(itemFilters, 0, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
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
        return world.isRemote ? ConstructorTile.TYPE.getValue() : type;
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

    private class NetworkFluidHandler implements IFluidHandler {
        private final FluidStack resource;

        public NetworkFluidHandler(FluidStack resource) {
            this.resource = resource;
        }

        @Override
        public int getTanks() {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getTankCapacity(int tank) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return network.extractFluid(resource, resource.getAmount(), compare, action == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return network.extractFluid(resource, resource.getAmount(), compare, action == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM);
        }
    }

    private static class ConstructorBlockItemUseContext extends BlockItemUseContext {
        public ConstructorBlockItemUseContext(World world, @Nullable PlayerEntity player, Hand hand, ItemStack stack, BlockRayTraceResult rayTraceResult) {
            super(world, player, hand, stack, rayTraceResult);
        }
    }
}

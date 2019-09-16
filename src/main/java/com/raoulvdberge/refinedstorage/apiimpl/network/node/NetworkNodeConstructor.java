package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.mojang.authlib.GameProfile;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkNodeConstructor extends NetworkNode implements IComparable, IType, ICoverable {
    public static final String ID = "constructor";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_DROP = "Drop";
    private static final String NBT_COVERS = "Covers";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBase itemFilters = new ItemHandlerBase(1, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(1, new ListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this)/* TODO, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK*/);

    private int compare = IComparer.COMPARE_NBT;
    private int type = IType.ITEMS;
    private boolean drop = false;

    private CoverManager coverManager = new CoverManager(this);

    public NetworkNodeConstructor(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.constructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            if (type == IType.ITEMS && !itemFilters.getStackInSlot(0).isEmpty()) {
                ItemStack item = itemFilters.getStackInSlot(0);

                BlockState block = SlotFilter.getBlockState(world, pos.offset(getDirection()), item);

                if (block != null) {
                    if (drop) {
                        dropItem();
                    } else {
                        placeBlock();
                    }
                } else {
                    if (item.getItem() == Items.FIREWORK_ROCKET && !drop) {
                        ItemStack took = network.extractItem(item, 1, Action.PERFORM);

                        if (took != null) {
                            world.addEntity(new FireworkRocketEntity(world, getDispensePositionX(), getDispensePositionY(), getDispensePositionZ(), took));
                        }
                    } else {
                        dropItem();
                    }
                }
            } else if (type == IType.FLUIDS && !fluidFilters.getFluid(0).isEmpty()) {
                /*TODO FluidStack stack = fluidFilters.getFluid(0);

                if (stack != null && stack.getFluid().getAttributes().canBePlacedInWorld()) {
                    BlockPos front = pos.offset(getDirection());

                    Block block = stack.getFluid().getAttributes();

                    if (world.isAirBlock(front) && block.canPlaceBlockAt(world, front)) {
                        FluidStack stored = network.getFluidStorageCache().getList().get(stack, compare);

                        if (stored != null && stored.amount >= Fluid.BUCKET_VOLUME) {
                            FluidStack took = network.extractFluid(stack, Fluid.BUCKET_VOLUME, compare, Action.PERFORM);

                            if (took != null) {
                                IBlockState state = block.getDefaultState();

                                if (state.getBlock() == Blocks.WATER) {
                                    state = Blocks.FLOWING_WATER.getDefaultState();
                                } else if (state.getBlock() == Blocks.LAVA) {
                                    state = Blocks.FLOWING_LAVA.getDefaultState();
                                }

                                if (!canPlace(front, state)) {
                                    return;
                                }

                                world.setBlockState(front, state, 1 | 2);
                            }
                        } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                            network.getCraftingManager().request(this, stack, Fluid.BUCKET_VOLUME);
                        }
                    }
                }*/
            }
        }
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

    private boolean canPlace(BlockPos pos, BlockState state) {
        BlockEvent.EntityPlaceEvent e = new BlockEvent.EntityPlaceEvent(new BlockSnapshot(world, pos, state), world.getBlockState(pos), getFakePlayer());

        return !MinecraftForge.EVENT_BUS.post(e);
    }

    private void placeBlock() {
        BlockPos front = pos.offset(getDirection());

        ItemStack item = itemFilters.getStackInSlot(0);

        ItemStack took = network.extractItem(item, 1, compare, Action.SIMULATE);

        if (took != null) {
            BlockState state = SlotFilter.getBlockState(world, front, took);

            // TODO if (state != null && world.isAirBlock(front) && state.getBlock().canPlaceBlockAt(world, front)) {
            if (false) {
                // TODO state = state.getBlock().getStateForPlacement(world, front, getDirection(), 0.5F, 0.5F, 0.5F, took.getMetadata(), FakePlayerFactory.getMinecraft((WorldServer) world), EnumHand.MAIN_HAND);

                if (!canPlace(front, state)) {
                    return;
                }

                took = network.extractItem(item, 1, compare, Action.PERFORM);

                if (took != null) {
                    if (item.getItem() instanceof BlockItem) {
                        /*((BlockItem) item.getItem()).tryPlace(new BlockItemUseContext(
                            took,
                            getFakePlayer(),
                            world,
                            front,
                            getDirection(),
                            0,
                            0,
                            0,
                            state
                        )); TODO! */
                    } else {
                        world.setBlockState(front, state, 1 | 2);

                        state.getBlock().onBlockPlacedBy(world, front, state, FakePlayerFactory.getMinecraft((ServerWorld) world), took);
                    }

                    // From ItemBlock#onItemUse
                    SoundType blockSound = state.getBlock().getSoundType(state, world, pos, null);
                    world.playSound(null, front, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);

                    /* TODO
                    if (state.getBlock() == Blocks.SKULL) {
                        world.setBlockState(front, world.getBlockState(front).withProperty(BlockSkull.FACING, getDirection()));

                        TileEntity tile = world.getTileEntity(front);

                        if (tile instanceof SkullTileEntity) {
                            SkullTileEntity skullTile = (SkullTileEntity) tile;

                            if (item.getItemDamage() == 3) {
                                GameProfile playerInfo = null;

                                if (item.hasTagCompound()) {
                                    CompoundNBT tag = item.getTagCompound();

                                    if (tag.contains("SkullOwner", 10)) {
                                        playerInfo = NBTUtil.readGameProfileFromNBT(tag.getCompound("SkullOwner"));
                                    } else if (tag.contains("SkullOwner", 8) && !tag.getString("SkullOwner").isEmpty()) {
                                        playerInfo = new GameProfile(null, tag.getString("SkullOwner"));
                                    }
                                }

                                skullTile.setPlayerProfile(playerInfo);
                            } else {
                                skullTile.setType(item.getMetadata());
                            }

                            Blocks.SKULL.checkWitherSpawn(world, front, skullTile);
                        }
                    }*/
                }
            }
        } else if (upgrades.hasUpgrade(ItemUpgrade.Type.CRAFTING)) {
            ItemStack craft = itemFilters.getStackInSlot(0);

            network.getCraftingManager().request(this, craft, 1);
        }
    }

    private void dropItem() {
        ItemStack took = network.extractItem(itemFilters.getStackInSlot(0), upgrades.getItemInteractCount(), Action.PERFORM);

        if (took != null) {
            DefaultDispenseItemBehavior.doDispense(world, took, 6, getDirection(), new Position(getDispensePositionX(), getDispensePositionY(), getDispensePositionZ()));
        } else if (upgrades.hasUpgrade(ItemUpgrade.Type.CRAFTING)) {
            ItemStack craft = itemFilters.getStackInSlot(0);

            network.getCraftingManager().request(this, craft, 1);
        }
    }

    // From BlockDispenser#getDispensePosition
    private double getDispensePositionX() {
        return (double) pos.getX() + 0.5D + 0.8D * (double) getDirection().getXOffset();
    }

    // From BlockDispenser#getDispensePosition
    private double getDispensePositionY() {
        return (double) pos.getY() + (getDirection() == Direction.DOWN ? 0.45D : 0.5D) + 0.8D * (double) getDirection().getYOffset();
    }

    // From BlockDispenser#getDispensePosition
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

        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 1, tag);

        tag.put(NBT_COVERS, coverManager.writeToNbt());

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

        if (tag.contains(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
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
        return new CombinedInvWrapper(upgrades, coverManager.getAsInventory());
    }

    @Override
    public boolean canConduct(@Nullable Direction direction) {
        return coverManager.canConduct(direction);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public int getType() {
        return world.isRemote ? TileConstructor.TYPE.getValue() : type;
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

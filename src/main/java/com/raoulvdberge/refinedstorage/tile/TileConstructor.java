package com.raoulvdberge.refinedstorage.tile;

import com.mojang.authlib.GameProfile;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.slot.SlotSpecimen;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileConstructor extends TileMultipartNode implements IComparable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileConstructor>() {
        @Override
        public Boolean getValue(TileConstructor tile) {
            return tile.drop;
        }
    }, new ITileDataConsumer<Boolean, TileConstructor>() {
        @Override
        public void setValue(TileConstructor tile, Boolean value) {
            tile.drop = value;

            tile.markDirty();
        }
    });

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_DROP = "Drop";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(1, this) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            item = getStackInSlot(slot) == null ? null : getStackInSlot(slot).copy();
            block = SlotSpecimen.getBlockState(getWorld(), pos.offset(getDirection()), getStackInSlot(slot));
        }
    };

    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(1, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private boolean drop = false;

    private IBlockState block;
    private ItemStack item;

    public TileConstructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.constructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            if (type == IType.ITEMS) {
                if (block != null) {
                    if (drop && item != null) {
                        dropItem();
                    } else {
                        placeBlock();
                    }
                } else if (item != null) {
                    if (item.getItem() == Items.FIREWORKS && !drop) {
                        ItemStack took = network.extractItem(item, 1, false);

                        if (took != null) {
                            getWorld().spawnEntity(new EntityFireworkRocket(getWorld(), getDispensePositionX(), getDispensePositionY(), getDispensePositionZ(), took));
                        }
                    } else {
                        dropItem();
                    }
                }
            } else if (type == IType.FLUIDS) {
                FluidStack stack = fluidFilters.getFluidStackInSlot(0);

                if (stack != null && stack.getFluid().canBePlacedInWorld()) {
                    BlockPos front = pos.offset(getDirection());

                    Block block = stack.getFluid().getBlock();

                    if (getWorld().isAirBlock(front) && block.canPlaceBlockAt(getWorld(), front)) {
                        FluidStack stored = network.getFluidStorageCache().getList().get(stack, compare);

                        if (stored != null && stored.amount >= Fluid.BUCKET_VOLUME) {
                            FluidStack took = network.extractFluid(stack, Fluid.BUCKET_VOLUME, compare, false);

                            if (took != null) {
                                IBlockState state = block.getDefaultState();

                                if (state.getBlock() == Blocks.WATER) {
                                    state = Blocks.FLOWING_WATER.getDefaultState();
                                } else if (state.getBlock() == Blocks.LAVA) {
                                    state = Blocks.FLOWING_LAVA.getDefaultState();
                                }

                                getWorld().setBlockState(front, state, 1 | 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeBlock() {
        BlockPos front = pos.offset(getDirection());

        if (getWorld().isAirBlock(front) && block.getBlock().canPlaceBlockAt(getWorld(), front)) {
            ItemStack took = network.extractItem(itemFilters.getStackInSlot(0), 1, compare, true);

            if (took != null) {
                @SuppressWarnings("deprecation")
                IBlockState state = block.getBlock().getStateFromMeta(took.getMetadata());

                BlockEvent.PlaceEvent e = new BlockEvent.PlaceEvent(new BlockSnapshot(getWorld(), front, state), getWorld().getBlockState(pos), FakePlayerFactory.getMinecraft((WorldServer) getWorld()), null);

                if (MinecraftForge.EVENT_BUS.post(e)) {
                    return;
                }

                network.extractItem(itemFilters.getStackInSlot(0), 1, compare, false);

                getWorld().setBlockState(front, state, 1 | 2);

                // From ItemBlock#onItemUse
                SoundType blockSound = block.getBlock().getSoundType(state, getWorld(), pos, null);
                getWorld().playSound(null, front, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);

                if (block.getBlock() == Blocks.SKULL) {
                    getWorld().setBlockState(front, getWorld().getBlockState(front).withProperty(BlockSkull.FACING, getDirection()));

                    TileEntity tile = getWorld().getTileEntity(front);

                    if (tile instanceof TileEntitySkull) {
                        TileEntitySkull skullTile = (TileEntitySkull) tile;

                        if (item.getItemDamage() == 3) {
                            GameProfile playerInfo = null;

                            if (item.hasTagCompound()) {
                                NBTTagCompound tag = item.getTagCompound();

                                if (tag.hasKey("SkullOwner", 10)) {
                                    playerInfo = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag("SkullOwner"));
                                } else if (tag.hasKey("SkullOwner", 8) && !tag.getString("SkullOwner").isEmpty()) {
                                    playerInfo = new GameProfile(null, tag.getString("SkullOwner"));
                                }
                            }

                            skullTile.setPlayerProfile(playerInfo);
                        } else {
                            skullTile.setType(item.getMetadata());
                        }

                        Blocks.SKULL.checkWitherSpawn(getWorld(), front, skullTile);
                    }

                }
            } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                ItemStack craft = itemFilters.getStackInSlot(0);

                network.scheduleCraftingTask(craft, 1, compare);
            }
        }
    }

    private void dropItem() {
        ItemStack took = network.extractItem(item, upgrades.getInteractStackSize(), false);

        if (took != null) {
            BehaviorDefaultDispenseItem.doDispense(getWorld(), took, 6, getDirection(), new PositionImpl(getDispensePositionX(), getDispensePositionY(), getDispensePositionZ()));
        } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
            ItemStack craft = itemFilters.getStackInSlot(0);

            network.scheduleCraftingTask(craft, 1, compare);
        }
    }

    // From BlockDispenser#getDispensePosition
    private double getDispensePositionX() {
        return (double) pos.getX() + 0.5D + 0.8D * (double) getDirection().getFrontOffsetX();
    }

    // From BlockDispenser#getDispensePosition
    private double getDispensePositionY() {
        return (double) pos.getY() + (getDirection() == EnumFacing.DOWN ? 0.45D : 0.5D) + 0.8D * (double) getDirection().getFrontOffsetY();
    }

    // From BlockDispenser#getDispensePosition
    private double getDispensePositionZ() {
        return (double) pos.getZ() + 0.5D + 0.8D * (double) getDirection().getFrontOffsetZ();
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
        tag.setBoolean(NBT_DROP, drop);

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

        if (tag.hasKey(NBT_DROP)) {
            drop = tag.getBoolean(NBT_DROP);
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
    public boolean hasConnectivityState() {
        return true;
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

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.item.ItemBlockBase;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public abstract class BlockBase extends Block {
    public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

    private String name;

    public BlockBase(String name) {
        super(Material.ROCK);

        this.name = name;

        setHardness(1.9F);
        setRegistryName(RS.ID, name);
        setCreativeTab(RS.INSTANCE.tab);
    }

    @Override
    public String getUnlocalizedName() {
        return "block." + RS.ID + ":" + name;
    }

    protected BlockStateContainer.Builder createBlockStateBuilder() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        if (getPlacementType() != null) {
            builder.add(DIRECTION);
        }

        return builder;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder().build();
    }

    public Item createItem() {
        return new ItemBlockBase(this, getPlacementType(), false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (getPlacementType() != null) {
            TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                return state.withProperty(DIRECTION, ((TileBase) tile).getDirection());
            }
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!world.isRemote && getPlacementType() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            tile.setDirection(getPlacementType().cycle(tile.getDirection()));

            RSUtils.updateBlock(world, pos);

            return true;
        }

        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        super.onBlockDestroyedByPlayer(world, pos, state);

        if (!world.isRemote) {
            dropContents(world, pos);
        }
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onBlockDestroyedByExplosion(world, pos, explosion);

        if (!world.isRemote) {
            dropContents(world, pos);
        }
    }

    private void dropContents(World world, BlockPos pos) {
        TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos);

        if (tile instanceof TileBase && ((TileBase) tile).getDrops() != null) {
            IItemHandler handler = ((TileBase) tile).getDrops();

            for (int i = 0; i < handler.getSlots(); ++i) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    protected boolean tryOpenNetworkGui(int guiId, EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
        return tryOpenNetworkGui(guiId, player, world, pos, facing, Permission.MODIFY);
    }

    protected boolean tryOpenNetworkGui(int guiId, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, Permission... permissions) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing)) {
            INetworkNodeProxy nodeProxy = CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing));
            INetworkNode node = nodeProxy.getNode();

            if (node.getNetwork() != null) {
                for (Permission permission : permissions) {
                    if (!node.getNetwork().getSecurityManager().hasPermission(permission, player)) {
                        RSUtils.sendNoPermissionMessage(player);

                        return false;
                    }
                }
            }
        }

        player.openGui(RS.INSTANCE, guiId, world, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null)) {
            INetworkNodeProxy nodeProxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null);
            INetworkNode node = nodeProxy.getNode();

            if (node.getNetwork() != null) {
                return entity instanceof EntityPlayer && node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, (EntityPlayer) entity);
            }
        }

        return super.canEntityDestroy(state, world, pos, entity);
    }

    public PlacementType getPlacementType() {
        return PlacementType.HORIZONTAL;
    }
}

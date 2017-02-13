package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class BlockStorageMonitor extends BlockNode {
    public BlockStorageMonitor() {
        super("storage_monitor");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack holding = player.inventory.getCurrentItem();

            if (player.isSneaking() || holding.isEmpty()) {
                tryOpenNetworkGui(RSGui.STORAGE_MONITOR, player, world, pos, side);
            } else {
                NetworkNodeStorageMonitor storageMonitor = ((TileStorageMonitor) world.getTileEntity(pos)).getNode();

                if (storageMonitor.getType() != IType.ITEMS) {
                    return false;
                }

                ItemStack displaying = storageMonitor.getItemFilter().getStackInSlot(0);

                if (storageMonitor.getNetwork() != null && !displaying.isEmpty() && API.instance().getComparer().isEqual(displaying, holding, storageMonitor.getCompare())) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, RSUtils.transformNullToEmpty(storageMonitor.getNetwork().insertItemTracked(holding, holding.getCount())));
                }
            }
        }

        return true;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        super.onBlockClicked(world, pos, player);

        if (!world.isRemote) {
            RayTraceResult rayResult = ForgeHooks.rayTraceEyes(player, ((EntityPlayerMP) player).interactionManager.getBlockReachDistance() + 1);

            if (rayResult == null) {
                return;
            }

            EnumFacing side = rayResult.sideHit;

            NetworkNodeStorageMonitor storageMonitor = ((TileStorageMonitor) world.getTileEntity(pos)).getNode();

            if (storageMonitor.getHolder().getDirection() != side || storageMonitor.getType() != IType.ITEMS) {
                return;
            }

            ItemStack displaying = storageMonitor.getItemFilter().getStackInSlot(0);

            int toExtract = player.isSneaking() ? 1 : 64;

            if (storageMonitor.getNetwork() != null && !displaying.isEmpty()) {
                ItemStack result = storageMonitor.getNetwork().extractItem(displaying, toExtract, storageMonitor.getCompare(), false);

                if (result != null) {
                    if (!player.inventory.addItemStackToInventory(result.copy())) {
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), result);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStorageMonitor();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WirelessCraftingMonitor implements ICraftingMonitor {
    private int controllerDimension;
    private BlockPos controller;

    public WirelessCraftingMonitor(int controllerDimension, ItemStack stack) {
        this.controllerDimension = controllerDimension;
        this.controller = new BlockPos(ItemWirelessCraftingMonitor.getX(stack), ItemWirelessCraftingMonitor.getY(stack), ItemWirelessCraftingMonitor.getZ(stack));
    }

    @Override
    public void onCancelled(EntityPlayerMP player, int id) {
        TileController controller = getController();

        if (controller != null) {
            controller.getItemGridHandler().onCraftingCancelRequested(player, id);
        }
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return null;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controller;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    private TileController getController() {
        World world = DimensionManager.getWorld(controllerDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(controller);

            return tile instanceof TileController ? (TileController) tile : null;
        }

        return null;
    }
}

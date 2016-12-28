package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingClear extends MessageHandlerPlayerToServer<MessageGridCraftingClear> implements IMessage {
    public MessageGridCraftingClear() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    public void handle(MessageGridCraftingClear message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            InventoryCrafting matrix = grid.getCraftingMatrix();

            if (grid.getType() == EnumGridType.CRAFTING && grid.getNetwork() != null && grid.getNetwork().getSecurityManager().hasPermission(Permission.INSERT, player)) {
                for (int i = 0; i < matrix.getSizeInventory(); ++i) {
                    ItemStack slot = matrix.getStackInSlot(i);

                    if (!slot.isEmpty()) {
                        matrix.setInventorySlotContents(i, RSUtils.getStack(grid.getNetwork().insertItem(slot, slot.getCount(), false)));
                    }
                }
            } else if (grid.getType() == EnumGridType.PATTERN) {
                for (int i = 0; i < matrix.getSizeInventory(); ++i) {
                    matrix.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
    }
}

package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotDisabled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class ContainerCraftingSettings extends ContainerBase {
    public ContainerCraftingSettings(EntityPlayer player, final ItemStack stack) {
        super(null, player);

        final ItemStack slot = ItemHandlerHelper.copyStackWithSize(stack, 1);

        addSlotToContainer(new SlotDisabled(new IInventory() {
            @Override
            public int getSizeInventory() {
                return 1;
            }

            @Nullable
            @Override
            public ItemStack getStackInSlot(int index) {
                return slot;
            }

            @Nullable
            @Override
            public ItemStack decrStackSize(int index, int count) {
                return null;
            }

            @Nullable
            @Override
            public ItemStack removeStackFromSlot(int index) {
                return null;
            }

            @Override
            public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
            }

            @Override
            public int getInventoryStackLimit() {
                return 0;
            }

            @Override
            public void markDirty() {
            }

            @Override
            public boolean isUsableByPlayer(EntityPlayer player) {
                return false;
            }

            @Override
            public void openInventory(EntityPlayer player) {
            }

            @Override
            public void closeInventory(EntityPlayer player) {
            }

            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack) {
                return false;
            }

            @Override
            public int getField(int id) {
                return 0;
            }

            @Override
            public void setField(int id, int value) {
            }

            @Override
            public int getFieldCount() {
                return 0;
            }

            @Override
            public void clear() {
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public boolean hasCustomName() {
                return false;
            }

            @Override
            public ITextComponent getDisplayName() {
                return null;
            }
        }, 0, 89, 48));
    }
}

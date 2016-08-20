package refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemHandlerProxy implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    private ItemHandlerBasic proxy;

    public ItemHandlerProxy(ItemHandlerBasic proxy) {
        this.proxy = proxy;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return proxy.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        proxy.deserializeNBT(nbt);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        proxy.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return proxy.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return proxy.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return proxy.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return proxy.extractItem(slot, amount, simulate);
    }
}

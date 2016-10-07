package refinedstorage;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.RSAPI;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;

import java.util.function.Function;

public final class RSUtils {
    public static void writeItemStack(ByteBuf buf, INetworkMaster network, ItemStack stack) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.stackSize);
        buf.writeInt(stack.getItemDamage());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
        buf.writeInt(RSAPI.instance().getItemStackHashCode(stack));
        buf.writeBoolean(network.hasPattern(stack));
    }

    public static void writeFluidStack(ByteBuf buf, FluidStack stack) {
        buf.writeInt(RSAPI.instance().getFluidStackHashCode(stack));
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
        buf.writeInt(stack.amount);
        ByteBufUtils.writeTag(buf, stack.tag);
    }

    public static void constructFromDrive(ItemStack disk, int slot, ItemStorageNBT[] itemStorages, FluidStorageNBT[] fluidStorages, Function<ItemStack, ItemStorageNBT> itemStorageSupplier, Function<ItemStack, FluidStorageNBT> fluidStorageNBTSupplier) {
        if (disk == null) {
            itemStorages[slot] = null;
            fluidStorages[slot] = null;
        } else {
            if (disk.getItem() == RSItems.STORAGE_DISK) {
                itemStorages[slot] = itemStorageSupplier.apply(disk);
            } else if (disk.getItem() == RSItems.FLUID_STORAGE_DISK) {
                fluidStorages[slot] = fluidStorageNBTSupplier.apply(disk);
            }
        }
    }
}

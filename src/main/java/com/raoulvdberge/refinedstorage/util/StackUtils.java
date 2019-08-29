package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class StackUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getCount());
        buf.writeShort(stack.getItemDamage());
        ByteBufUtils.writeTag(buf, stack.getItem().getNBTShareTag(stack));
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        ItemStack stack = new ItemStack(Item.getItemById(buf.readInt()), buf.readInt(), buf.readShort());
        stack.setTagCompound(ByteBufUtils.readTag(buf));
        return stack;
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack, @Nullable INetwork network, boolean displayCraftText) {
        writeItemStack(buf, stack);

        buf.writeInt(API.instance().getItemStackHashCode(stack));

        if (network != null) {
            buf.writeBoolean(network.getCraftingManager().getPattern(stack) != null);
            buf.writeBoolean(displayCraftText);
        } else {
            buf.writeBoolean(false);
            buf.writeBoolean(false);
        }
    }

    public static void writeFluidStackAndHash(ByteBuf buf, FluidStack stack) {
        buf.writeInt(API.instance().getFluidStackHashCode(stack));

        writeFluidStack(buf, stack);
    }

    public static void writeFluidStack(ByteBuf buf, FluidStack stack) {
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
        buf.writeInt(stack.amount);
        ByteBufUtils.writeTag(buf, stack.tag);
    }

    public static FluidStack readFluidStack(ByteBuf buf) {
        return new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), ByteBufUtils.readTag(buf));
    }

    public static Pair<Integer, FluidStack> readFluidStackAndHash(ByteBuf buf) {
        return Pair.of(buf.readInt(), readFluidStack(buf));
    }

    public static ItemStack nullToEmpty(@Nullable ItemStack stack) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Nullable
    public static ItemStack emptyToNull(@Nonnull ItemStack stack) {
        return stack.isEmpty() ? null : stack;
    }

    @SuppressWarnings("unchecked")
    public static void createStorages(World world, ItemStack diskStack, int slot, IStorageDisk<ItemStack>[] itemDisks, IStorageDisk<FluidStack>[] fluidDisks, Function<IStorageDisk<ItemStack>, IStorageDisk> itemDiskWrapper, Function<IStorageDisk<FluidStack>, IStorageDisk> fluidDiskWrapper) {
        if (diskStack.isEmpty()) {
            itemDisks[slot] = null;
            fluidDisks[slot] = null;
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager(world).getByStack(diskStack);

            if (disk != null) {
                switch (((IStorageDiskProvider) diskStack.getItem()).getType()) {
                    case ITEM: {
                        itemDisks[slot] = itemDiskWrapper.apply(disk);
                        break;
                    }
                    case FLUID: {
                        fluidDisks[slot] = fluidDiskWrapper.apply(disk);
                        break;
                    }
                }
            } else {
                itemDisks[slot] = null;
                fluidDisks[slot] = null;
            }
        }
    }

    public static void writeItems(IItemHandler handler, int id, CompoundNBT tag, Function<ItemStack, CompoundNBT> serializer) {
        ListNBT tagList = new ListNBT();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                CompoundNBT stackTag = serializer.apply(handler.getStackInSlot(i));

                stackTag.putInt(NBT_SLOT, i);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void writeItems(IItemHandler handler, int id, CompoundNBT tag) {
        writeItems(handler, id, tag, stack -> stack.writeToNBT(new CompoundNBT()));
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundNBT tag, Function<CompoundNBT, ItemStack> deserializer) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.hasKey(name)) {
            ListNBT tagList = tag.getList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInteger(NBT_SLOT);

                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.setStackInSlot(slot, deserializer.apply(tagList.getCompound(i)));
                }
            }
        }
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundNBT tag) {
        readItems(handler, id, tag, ItemStack::new);
    }

    public static void writeItems(IInventory inventory, int id, CompoundNBT tag) {
        ListNBT tagList = new ListNBT();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                CompoundNBT stackTag = new CompoundNBT();

                stackTag.putInt(NBT_SLOT, i);

                inventory.getStackInSlot(i).writeToNBT(stackTag);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void readItems(IInventory inventory, int id, CompoundNBT tag) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.hasKey(name)) {
            ListNBT tagList = tag.getList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInteger(NBT_SLOT);

                ItemStack stack = new ItemStack(tagList.getCompound(i));

                if (!stack.isEmpty()) {
                    inventory.setInventorySlotContents(slot, stack);
                }
            }
        }
    }

    public static boolean hasFluidBucket(FluidStack stack) {
        return stack.getFluid() == FluidRegistry.WATER || stack.getFluid() == FluidRegistry.LAVA || stack.getFluid().getName().equals("milk") || FluidRegistry.getBucketFluids().contains(stack.getFluid());
    }

    public static FluidStack copy(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.setAmount(size);
        return copy;
    }

    public static FluidStack copy(@Nullable FluidStack stack) {
        return stack == null ? null : stack.copy();
    }

    public static Pair<ItemStack, FluidStack> getFluid(ItemStack stack, boolean simulate) {
        // We won't have the capability on stacks with size bigger than 1.
        if (stack.getCount() > 1) {
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        }

        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

            FluidStack result = fluidHandler.drain(Fluid.BUCKET_VOLUME, !simulate);

            return Pair.of(fluidHandler.getContainer(), result);
        }

        return Pair.of(null, null);
    }

    private static final String NBT_ITEM_TYPE = "Type";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_DAMAGE = "Damage";
    private static final String NBT_ITEM_NBT = "NBT";
    private static final String NBT_ITEM_CAPS = "Caps";

    public static CompoundNBT serializeStackToNbt(@Nonnull ItemStack stack) {
        CompoundNBT dummy = new CompoundNBT();

        CompoundNBT itemTag = new CompoundNBT();

        itemTag.putInt(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
        itemTag.putInt(NBT_ITEM_QUANTITY, stack.getCount());
        itemTag.putInt(NBT_ITEM_DAMAGE, stack.getItemDamage());

        if (stack.hasTagCompound()) {
            itemTag.put(NBT_ITEM_NBT, stack.getTagCompound());
        }

        stack.writeToNBT(dummy);

        if (dummy.hasKey("ForgeCaps")) {
            itemTag.put(NBT_ITEM_CAPS, dummy.getTag("ForgeCaps"));
        }

        dummy.removeTag("ForgeCaps");

        return itemTag;
    }

    @Nonnull
    public static ItemStack deserializeStackFromNbt(CompoundNBT tag) {
        ItemStack stack = new ItemStack(
            Item.getItemById(tag.getInteger(NBT_ITEM_TYPE)),
            tag.getInteger(NBT_ITEM_QUANTITY),
            tag.getInteger(NBT_ITEM_DAMAGE),
            tag.hasKey(NBT_ITEM_CAPS) ? tag.getCompound(NBT_ITEM_CAPS) : null
        );

        stack.setTagCompound(tag.hasKey(NBT_ITEM_NBT) ? tag.getCompound(NBT_ITEM_NBT) : null);

        return stack;
    }
}

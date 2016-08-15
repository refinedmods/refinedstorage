package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.block.EnumFluidStorageType;
import refinedstorage.tile.TileFluidStorage;

import java.util.List;

public class ItemBlockFluidStorage extends ItemBlockBase {
    public ItemBlockFluidStorage() {
        super(RefinedStorageBlocks.FLUID_STORAGE, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        EnumFluidStorageType type = EnumFluidStorageType.getById(stack.getMetadata());

        if (type != null && isValid(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileFluidStorage.NBT_STORAGE);

            if (type == EnumFluidStorageType.TYPE_CREATIVE) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", FluidStorageNBT.getStoredFromNBT(tag)));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", FluidStorageNBT.getStoredFromNBT(tag), type.getCapacity()));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        EnumFluidStorageType type = EnumFluidStorageType.getById(stack.getMetadata());

        if (type != null && isValid(stack) && FluidStorageNBT.getStoredFromNBT(stack.getTagCompound().getCompoundTag(TileFluidStorage.NBT_STORAGE)) == 0 && stack.getMetadata() != ItemFluidStorageDisk.TYPE_CREATIVE && !world.isRemote && player.isSneaking()) {
            ItemStack storagePart = new ItemStack(RefinedStorageItems.FLUID_STORAGE_PART, 1, stack.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            ItemStack processor = new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC);

            if (!player.inventory.addItemStackToInventory(processor.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), processor);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RefinedStorageBlocks.MACHINE_CASING));
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private static boolean isValid(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileFluidStorage.NBT_STORAGE);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(TileFluidStorage.NBT_STORAGE, FluidStorageNBT.createNBT());
        stack.setTagCompound(tag);
        return stack;
    }
}

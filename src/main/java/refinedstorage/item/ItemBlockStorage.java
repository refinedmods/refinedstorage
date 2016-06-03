package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.EnumStorageType;
import refinedstorage.storage.NBTStorage;
import refinedstorage.tile.TileStorage;

import java.util.List;

public class ItemBlockStorage extends ItemBlockBase {
    public ItemBlockStorage() {
        super(RefinedStorageBlocks.STORAGE, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        EnumStorageType type = EnumStorageType.getById(stack.getMetadata());

        if (type != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileStorage.NBT_STORAGE)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE);

            if (type == EnumStorageType.TYPE_CREATIVE) {
                list.add(I18n.format("misc.refinedstorage:storage.stored", NBTStorage.getStoredFromNBT(tag)));
            } else {
                list.add(I18n.format("misc.refinedstorage:storage.stored_capacity", NBTStorage.getStoredFromNBT(tag), type.getCapacity()));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(TileStorage.NBT_STORAGE, NBTStorage.createNBT());
        stack.setTagCompound(tag);
        return stack;
    }
}

package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
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
                list.add(String.format(I18n.translateToLocal("misc.refinedstorage:storage.stored"), NBTStorage.getStored(tag)));
            } else {
                list.add(String.format(I18n.translateToLocal("misc.refinedstorage:storage.stored_capacity"), NBTStorage.getStored(tag), type.getCapacity()));
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

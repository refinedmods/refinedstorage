package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.EnumControllerType;
import refinedstorage.tile.controller.TileController;

import java.util.List;

public class ItemBlockController extends ItemBlockBase {
    public ItemBlockController() {
        super(RefinedStorageBlocks.CONTROLLER, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (stack.getMetadata() != EnumControllerType.CREATIVE.getId()) {
            int energyStored = 0;

            if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileController.NBT_ENERGY)) {
                energyStored = stack.getTagCompound().getInteger(TileController.NBT_ENERGY);
            }

            list.add(I18n.format("misc.refinedstorage:energy_stored", energyStored, TileController.ENERGY_CAPACITY));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setInteger(TileController.NBT_ENERGY, stack.getMetadata() == EnumControllerType.CREATIVE.getId() ? TileController.ENERGY_CAPACITY : 0);

        return stack;
    }
}

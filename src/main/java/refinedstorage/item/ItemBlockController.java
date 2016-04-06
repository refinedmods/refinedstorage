package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.EnumControllerType;
import refinedstorage.tile.TileController;

import java.util.List;

public class ItemBlockController extends ItemBlockBase {
    public ItemBlockController() {
        super(RefinedStorageBlocks.CONTROLLER, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        EnumControllerType type = stack.getMetadata() == EnumControllerType.CREATIVE.getId() ? EnumControllerType.CREATIVE : EnumControllerType.NORMAL;

        int energyStored = 0;
        int capacity = TileController.ENERGY_CAPACITY;

        if (type == EnumControllerType.CREATIVE) {
            energyStored = capacity;
        } else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileController.NBT_ENERGY)) {
            energyStored = stack.getTagCompound().getInteger(TileController.NBT_ENERGY);
        }

        list.add(I18n.format("misc.refinedstorage:energy_stored", energyStored, capacity));
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        EnumControllerType type = stack.getMetadata() == EnumControllerType.CREATIVE.getId() ? EnumControllerType.CREATIVE : EnumControllerType.NORMAL;

        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setInteger(TileController.NBT_ENERGY, type == EnumControllerType.CREATIVE ? TileController.ENERGY_CAPACITY : 0);

        return stack;
    }
}

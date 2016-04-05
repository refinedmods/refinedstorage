package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.block.EnumControllerType;
import refinedstorage.block.EnumStorageType;
import refinedstorage.tile.TileController;

import java.util.List;

/**
 * Created by zyberwax on 05.04.2016.
 */
public class ItemBlockController extends ItemBlockBase {
    public ItemBlockController() {
        super(RefinedStorageBlocks.CONTROLLER, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        EnumControllerType type = stack.getMetadata() == 1 ? EnumControllerType.CREATIVE : EnumControllerType.NORMAL;

        int energyStored = 0;
        int capacity     = TileController.ENERGY_CAPACITY;
        int percent      = 0;

        if(type == EnumControllerType.CREATIVE) {
            energyStored = capacity;
            percent = 100;
        }
        else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileController.NBT_ENERGY)) {
            NBTTagCompound tag = stack.getTagCompound();

            energyStored = tag.getInteger(TileController.NBT_ENERGY);
            percent = (int)((float)energyStored / (capacity) * 100);
        }
        //TODO: Format numbers ?
        list.add("RF: " + energyStored + "/" + capacity + " (" + percent + "%)");

    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        EnumControllerType type = stack.getMetadata() == 1 ? EnumControllerType.CREATIVE : EnumControllerType.NORMAL;
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) tag = new NBTTagCompound();

        tag.setInteger(TileController.NBT_ENERGY, type == EnumControllerType.CREATIVE ? TileController.ENERGY_CAPACITY : 0);

        return stack;
    }
}

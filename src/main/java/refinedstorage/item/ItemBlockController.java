package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
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

        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileController.NBT_ENERGY)) {
            NBTTagCompound tag = stack.getTagCompound();

            int energyStored = tag.getInteger(TileController.NBT_ENERGY);
            int capacity     = TileController.ENERGY_CAPACITY;
            int percent      = (int)((float)energyStored / (capacity) * 100);

            //TODO: Format numbers ?
            list.add("RF: " + energyStored + "/" + capacity + "(" + percent + "%)");
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        return stack;
    }
}

package refinedstorage.container.slot;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.lang.reflect.Field;

public class SlotSpecimen extends SlotItemHandler {
    public static final int SPECIMEN_SIZE = 1;
    public static final int SPECIMEN_BLOCK = 2;

    private int flags = 0;

    public SlotSpecimen(IItemHandler handler, int id, int x, int y, int flags) {
        super(handler, id, x, y);

        this.flags = flags;
    }

    public SlotSpecimen(IItemHandler handler, int id, int x, int y) {
        this(handler, id, x, y, 0);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return super.isItemValid(stack) && (isBlockOnly() ? (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemBlockSpecial || stack.getItem() instanceof IPlantable) : true);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (stack != null && !isWithSize()) {
            stack.stackSize = 1;
        }

        super.putStack(stack);
    }

    public boolean isWithSize() {
        return (flags & SPECIMEN_SIZE) == SPECIMEN_SIZE;
    }

    public boolean isBlockOnly() {
        return (flags & SPECIMEN_BLOCK) == SPECIMEN_BLOCK;
    }

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos, ItemStack stack) {
        if (stack != null) {
            Item item = stack.getItem();

            if (item instanceof ItemBlockSpecial) {
                try {
                    Field f = ((ItemBlockSpecial) item).getClass().getDeclaredField("block");
                    f.setAccessible(true);
                    return ((Block) f.get(item)).getDefaultState();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    // NO OP
                }
            } else if (item instanceof ItemBlock) {
                return (((ItemBlock) item).getBlock()).getDefaultState();
            } else if (item instanceof IPlantable) {
                return ((IPlantable) item).getPlant(world, pos);
            }
        }

        return null;
    }
}

package refinedstorage.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileGrid;

import java.util.List;

public class ItemWirelessGrid extends ItemBase {
    public static final String NBT_GRID_X = "GridX";
    public static final String NBT_GRID_Y = "GridY";
    public static final String NBT_GRID_Z = "GridZ";

    public ItemWirelessGrid() {
        super("wireless_grid");

        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (isValid(stack)) {
            list.add(I18n.translateToLocalFormatted("misc.refinedstorage:wireless_grid.tooltip.0", getX(stack)));
            list.add(I18n.translateToLocalFormatted("misc.refinedstorage:wireless_grid.tooltip.1", getY(stack)));
            list.add(I18n.translateToLocalFormatted("misc.refinedstorage:wireless_grid.tooltip.2", getZ(stack)));
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == RefinedStorageBlocks.GRID) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setInteger(NBT_GRID_X, pos.getX());
            tag.setInteger(NBT_GRID_Y, pos.getY());
            tag.setInteger(NBT_GRID_Z, pos.getZ());

            stack.setTagCompound(tag);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (isValid(stack)) {
                if (isInRange(stack, player)) {
                    TileEntity tile = world.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)));

                    if (tile instanceof TileGrid) {
                        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.GRID, world, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());

                        return new ActionResult(EnumActionResult.PASS, stack);
                    } else {
                        player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.not_found")));
                    }
                } else {
                    player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.out_of_range")));
                }
            } else {
                player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.not_found")));
            }

            return new ActionResult(EnumActionResult.FAIL, stack);
        } else {
            return new ActionResult(EnumActionResult.PASS, stack);
        }
    }

    public static int getX(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_GRID_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_GRID_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_GRID_Z);
    }

    public static boolean isInRange(ItemStack stack, EntityPlayer player) {
        return (int) Math.sqrt(Math.pow(getX(stack) - player.posX, 2) + Math.pow(getY(stack) - player.posY, 2) + Math.pow(getZ(stack) - player.posZ, 2)) < 64;
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_GRID_X) && stack.getTagCompound().hasKey(NBT_GRID_Y) && stack.getTagCompound().hasKey(NBT_GRID_Z);
    }
}

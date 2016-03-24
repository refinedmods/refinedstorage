package refinedstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.TileGrid;
import refinedstorage.tile.TileWirelessTransmitter;

import java.util.List;

public class ItemWirelessGrid extends ItemBase {
    public static final String NBT_WIRELESS_TRANSMITTER_X = "WirelessTransmitterX";
    public static final String NBT_WIRELESS_TRANSMITTER_Y = "WirelessTransmitterY";
    public static final String NBT_WIRELESS_TRANSMITTER_Z = "WirelessTransmitterZ";

    public ItemWirelessGrid() {
        super("wireless_grid");

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (isValid(stack)) {
            list.add(I18n.translateToLocalFormatted("misc.refinedstorage:wireless_grid.tooltip", getX(stack), getY(stack), getZ(stack)));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (isValid(stack)) {
                if (isInRange(stack, player)) {
                    int x = getX(stack);
                    int y = getY(stack);
                    int z = getZ(stack);

                    TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

                    if (tile instanceof TileWirelessTransmitter) {
                        TileWirelessTransmitter wirelessTransmitter = (TileWirelessTransmitter) tile;

                        if (wirelessTransmitter.isWorking()) {
                            TileGrid grid = wirelessTransmitter.getGrid(stack.getItemDamage() == 1 ? EnumGridType.CRAFTING : EnumGridType.NORMAL);

                            if (grid == null) {
                                player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.no_grid." + stack.getItemDamage())));
                            } else {
                                player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.GRID, world, grid.getPos().getX(), grid.getPos().getY(), grid.getPos().getZ());

                                return new ActionResult(EnumActionResult.SUCCESS, stack);
                            }
                        } else {
                            player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.not_working")));
                        }
                    } else {
                        player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.not_found")));
                    }
                } else {
                    player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.out_of_range")));
                }
            } else {
                player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("misc.refinedstorage:wireless_grid.not_set." + stack.getItemDamage())));
            }

            return new ActionResult(EnumActionResult.FAIL, stack);
        } else {
            return new ActionResult(EnumActionResult.PASS, stack);
        }
    }

    public static int getX(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_WIRELESS_TRANSMITTER_Z);
    }

    public static boolean isInRange(ItemStack stack, EntityPlayer player) {
        return (int) Math.sqrt(Math.pow(getX(stack) - player.posX, 2) + Math.pow(getY(stack) - player.posY, 2) + Math.pow(getZ(stack) - player.posZ, 2)) < 64;
    }

    public static boolean isValid(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_X) && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_Y) && stack.getTagCompound().hasKey(NBT_WIRELESS_TRANSMITTER_Z);
    }
}

package refinedstorage.item;

import cofh.api.energy.ItemEnergyContainer;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.api.network.NetworkMasterRegistry;
import refinedstorage.tile.grid.TileGrid;

import java.util.List;

public class ItemWirelessGrid extends ItemEnergyContainer {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CREATIVE = 1;

    public static final String NBT_CONTROLLER_X = "ControllerX";
    public static final String NBT_CONTROLLER_Y = "ControllerY";
    public static final String NBT_CONTROLLER_Z = "ControllerZ";
    public static final String NBT_DIMENSION_ID = "DimensionID";
    public static final String NBT_SORTING_TYPE = "SortingType";
    public static final String NBT_SORTING_DIRECTION = "SortingDirection";
    public static final String NBT_SEARCH_BOX_MODE = "SearchBoxMode";

    public static final int USAGE_OPEN = 30;
    public static final int USAGE_PULL = 3;
    public static final int USAGE_PUSH = 3;

    public ItemWirelessGrid() {
        super(3200);

        addPropertyOverride(new ResourceLocation("connected"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, World world, EntityLivingBase entity) {
                return (entity != null && hasValidNBT(stack) && getDimensionId(stack) == entity.dimension) ? 1.0f : 0.0f;
            }
        });

        setRegistryName(RefinedStorage.ID, "wireless_grid");
        setMaxDamage(3200);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RefinedStorage.TAB);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1d - ((double) getEnergyStored(stack) / (double) getMaxEnergyStored(stack));
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return stack.getItemDamage() != TYPE_CREATIVE;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // NO OP
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, TYPE_NORMAL));

        ItemStack fullyCharged = new ItemStack(item, 1, TYPE_NORMAL);
        receiveEnergy(fullyCharged, getMaxEnergyStored(fullyCharged), false);
        list.add(fullyCharged);

        list.add(new ItemStack(item, 1, TYPE_CREATIVE));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (stack.getItemDamage() != TYPE_CREATIVE) {
            list.add(I18n.format("misc.refinedstorage:energy_stored", getEnergyStored(stack), getMaxEnergyStored(stack)));
        }

        if (hasValidNBT(stack)) {
            list.add(I18n.format("misc.refinedstorage:wireless_grid.tooltip.0", getX(stack)));
            list.add(I18n.format("misc.refinedstorage:wireless_grid.tooltip.1", getY(stack)));
            list.add(I18n.format("misc.refinedstorage:wireless_grid.tooltip.2", getZ(stack)));
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();

        if (block == RefinedStorageBlocks.CONTROLLER) {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag == null) {
                tag = new NBTTagCompound();
            }

            tag.setInteger(NBT_CONTROLLER_X, pos.getX());
            tag.setInteger(NBT_CONTROLLER_Y, pos.getY());
            tag.setInteger(NBT_CONTROLLER_Z, pos.getZ());
            tag.setInteger(NBT_DIMENSION_ID, player.dimension);
            tag.setInteger(NBT_SORTING_DIRECTION, TileGrid.SORTING_DIRECTION_DESCENDING);
            tag.setInteger(NBT_SORTING_TYPE, TileGrid.SORTING_TYPE_NAME);
            tag.setInteger(NBT_SEARCH_BOX_MODE, TileGrid.SEARCH_BOX_MODE_NORMAL);

            stack.setTagCompound(tag);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && hasValidNBT(stack) && getDimensionId(stack) == player.dimension) {
            NetworkMaster network = NetworkMasterRegistry.get(new BlockPos(getX(stack), getY(stack), getZ(stack)), player.worldObj.provider.getDimension());

            if (network != null) {
                if (network.getWirelessGridHandler().handleOpen(player, hand)) {
                    return new ActionResult(EnumActionResult.SUCCESS, stack);
                } else {
                    player.addChatComponentMessage(new TextComponentTranslation("misc.refinedstorage:wireless_grid.out_of_range"));
                }
            } else {
                player.addChatComponentMessage(new TextComponentTranslation("misc.refinedstorage:wireless_grid.not_found"));
            }
        }

        return new ActionResult(EnumActionResult.PASS, stack);
    }

    public static int getDimensionId(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_DIMENSION_ID);
    }

    public static int getX(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_Z);
    }

    public static int getSortingType(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_SORTING_TYPE);
    }

    public static int getSortingDirection(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_SORTING_DIRECTION);
    }

    public static int getSearchBoxMode(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_SEARCH_BOX_MODE);
    }

    private static boolean hasValidNBT(ItemStack stack) {
        return stack.hasTagCompound()
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_X)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Y)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Z)
            && stack.getTagCompound().hasKey(NBT_DIMENSION_ID)
            && stack.getTagCompound().hasKey(NBT_SORTING_DIRECTION)
            && stack.getTagCompound().hasKey(NBT_SORTING_TYPE)
            && stack.getTagCompound().hasKey(NBT_SEARCH_BOX_MODE);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem()) {
            if (hasValidNBT(oldStack) && hasValidNBT(newStack)) {
                if (getX(oldStack) == getX(newStack) && getY(oldStack) == getY(newStack) && getZ(oldStack) == getZ(newStack) && getDimensionId(oldStack) == getDimensionId(newStack)) {
                    return false;
                }
            }
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + RefinedStorage.ID + ":wireless_grid";
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName() + "." + stack.getItemDamage();
    }
}

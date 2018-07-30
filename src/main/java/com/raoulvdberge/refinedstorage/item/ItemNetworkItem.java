package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import com.raoulvdberge.refinedstorage.item.info.IItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class ItemNetworkItem extends ItemEnergyItem implements INetworkItemProvider {
    private static final String NBT_CONTROLLER_X = "ControllerX";
    private static final String NBT_CONTROLLER_Y = "ControllerY";
    private static final String NBT_CONTROLLER_Z = "ControllerZ";
    private static final String NBT_DIMENSION_ID = "DimensionID";

    public ItemNetworkItem(IItemInfo info, int energyCapacity) {
        super(info, energyCapacity);

        addPropertyOverride(new ResourceLocation("connected"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            applyNetwork(stack, n -> n.getNetworkItemHandler().onOpen(player, hand), player::sendMessage);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    public void applyNetwork(ItemStack stack, Consumer<INetwork> networkConsumer, Consumer<TextComponentTranslation> errorConsumer) {
        if (!isValid(stack)) {
            errorConsumer.accept(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
        } else {
            World networkWorld = DimensionManager.getWorld(getDimensionId(stack));

            TileEntity network;

            if (networkWorld != null && ((network = networkWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)))) instanceof INetwork)) {
                networkConsumer.accept((INetwork) network);
            } else {
                errorConsumer.accept(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack)) {
            tooltip.add(I18n.format("misc.refinedstorage:network_item.tooltip", getX(stack), getY(stack), getZ(stack)));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        Block block = world.getBlockState(pos).getBlock();

        if (block == RSBlocks.CONTROLLER) {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag == null) {
                tag = new NBTTagCompound();
            }

            tag.setInteger(NBT_CONTROLLER_X, pos.getX());
            tag.setInteger(NBT_CONTROLLER_Y, pos.getY());
            tag.setInteger(NBT_CONTROLLER_Z, pos.getZ());
            tag.setInteger(NBT_DIMENSION_ID, player.dimension);

            stack.setTagCompound(tag);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
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

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound()
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_X)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Y)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Z)
            && stack.getTagCompound().hasKey(NBT_DIMENSION_ID);
    }
}

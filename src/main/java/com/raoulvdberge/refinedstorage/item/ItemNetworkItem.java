package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemProvider;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.NetworkItemEnergyForge;
import com.raoulvdberge.refinedstorage.integration.tesla.IntegrationTesla;
import com.raoulvdberge.refinedstorage.integration.tesla.NetworkItemEnergyTesla;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemNetworkItem extends ItemBase implements INetworkItemProvider {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CREATIVE = 1;

    private static final String NBT_CONTROLLER_X = "ControllerX";
    private static final String NBT_CONTROLLER_Y = "ControllerY";
    private static final String NBT_CONTROLLER_Z = "ControllerZ";
    private static final String NBT_DIMENSION_ID = "DimensionID";

    public ItemNetworkItem(String name) {
        super(name);

        setMaxDamage(3200);
        setMaxStackSize(1);
        setHasSubtypes(true);

        addPropertyOverride(new ResourceLocation("connected"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            if (!isValid(stack)) {
                player.sendMessage(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
            } else {
                World controllerWorld = DimensionManager.getWorld(getDimensionId(stack));

                TileEntity controller;

                if (controllerWorld != null && ((controller = controllerWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)))) instanceof TileController)) {
                    ((TileController) controller).getNetworkItemHandler().onOpen(player, controllerWorld, hand);
                } else {
                    player.sendMessage(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
                }
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new NetworkItemCapabilityProvider(stack);
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
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

        return 1D - ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

        return MathHelper.hsvToRGB(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
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
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(item, 1, TYPE_NORMAL));

        ItemStack fullyCharged = new ItemStack(item, 1, TYPE_NORMAL);

        IEnergyStorage energy = fullyCharged.getCapability(CapabilityEnergy.ENERGY, null);
        energy.receiveEnergy(energy.getMaxEnergyStored(), false);

        list.add(fullyCharged);

        list.add(new ItemStack(item, 1, TYPE_CREATIVE));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        if (stack.getItemDamage() != TYPE_CREATIVE) {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

            tooltip.add(I18n.format("misc.refinedstorage:energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()));
        }

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

            initializeDefaults(tag);

            stack.setTagCompound(tag);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    public void initializeDefaults(NBTTagCompound tag) {
        // NO OP
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
        if (oldStack.getItem() == newStack.getItem()) {
            if (isValid(oldStack) && isValid(newStack)) {
                if (getX(oldStack) == getX(newStack) && getY(oldStack) == getY(newStack) && getZ(oldStack) == getZ(newStack) && getDimensionId(oldStack) == getDimensionId(newStack)) {
                    return false;
                }
            }
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound()
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_X)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Y)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Z)
            && stack.getTagCompound().hasKey(NBT_DIMENSION_ID);
    }

    private class NetworkItemCapabilityProvider implements ICapabilityProvider {
        private ItemStack stack;

        public NetworkItemCapabilityProvider(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityEnergy.ENERGY ||
                (IntegrationTesla.isLoaded() && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER));
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY) {
                return (T) new NetworkItemEnergyForge(stack, 3200);
            }

            if (IntegrationTesla.isLoaded() && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
                return (T) new NetworkItemEnergyTesla(stack);
            }

            return null;
        }
    }
}

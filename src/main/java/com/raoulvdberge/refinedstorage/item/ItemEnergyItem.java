package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.integration.forgeenergy.NetworkItemEnergyForge;
import com.raoulvdberge.refinedstorage.integration.tesla.IntegrationTesla;
import com.raoulvdberge.refinedstorage.integration.tesla.NetworkItemEnergyTesla;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemEnergyItem extends ItemBase {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CREATIVE = 1;

    public ItemEnergyItem(String name) {
        super(name);

        setMaxDamage(3200);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound tag) {
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
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

        if (stack.getItemDamage() != TYPE_CREATIVE) {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

            tooltip.add(I18n.format("misc.refinedstorage:energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()));
        }
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
                return CapabilityEnergy.ENERGY.cast(new NetworkItemEnergyForge(stack, 3200));
            }

            if (IntegrationTesla.isLoaded()) {
                if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
                    return TeslaCapabilities.CAPABILITY_HOLDER.cast(new NetworkItemEnergyTesla(stack));
                }

                if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
                    return TeslaCapabilities.CAPABILITY_CONSUMER.cast(new NetworkItemEnergyTesla(stack));
                }
            }

            return null;
        }
    }
}

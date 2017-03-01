package com.raoulvdberge.refinedstorage.integration.craftingtweaks;

import com.google.common.base.Function;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public final class IntegrationCraftingTweaks {
    private static final String ID = "craftingtweaks";

    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }

    public static void register() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("ContainerClass", ContainerGrid.class.getName());
        tag.setString("ContainerCallback", ContainerCallback.class.getName());
        tag.setInteger("GridSlotNumber", 36);
        tag.setString("AlignToGrid", "left");

        FMLInterModComms.sendMessage(ID, "RegisterProviderV2", tag);
    }

    public static class ContainerCallback implements Function<ContainerGrid, Boolean> {
        @Override
        public Boolean apply(ContainerGrid containerGrid) {
            return containerGrid.getGrid().getType() == GridType.CRAFTING;
        }
    }
}

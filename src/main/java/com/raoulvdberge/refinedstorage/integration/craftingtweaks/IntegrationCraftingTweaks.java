package com.raoulvdberge.refinedstorage.integration.craftingtweaks;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.container.slot.SlotGridCrafting;
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
        tag.setString("ValidContainerPredicate", ValidContainerPredicate.class.getName());
        tag.setString("GetGridStartFunction", GetGridStartFunction.class.getName());
        tag.setString("AlignToGrid", "left");

        FMLInterModComms.sendMessage(ID, "RegisterProviderV3", tag);
    }

    public static class ValidContainerPredicate implements Predicate<ContainerGrid> {
        @Override
        public boolean apply(ContainerGrid containerGrid) {
            return containerGrid.getGrid().getType() == GridType.CRAFTING;
        }
    }

    public static class GetGridStartFunction implements Function<ContainerGrid, Integer> {
        @Override
        public Integer apply(ContainerGrid containerGrid) {
            for(int i = 0; i < containerGrid.inventorySlots.size(); i++) {
                if(containerGrid.inventorySlots.get(i) instanceof SlotGridCrafting) {
                    return i;
                }
            }
            return 0;
        }
    }
}

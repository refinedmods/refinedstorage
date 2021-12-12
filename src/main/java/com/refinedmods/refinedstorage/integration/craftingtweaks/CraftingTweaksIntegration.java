package com.refinedmods.refinedstorage.integration.craftingtweaks;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

import java.util.function.Function;
import java.util.function.Predicate;

public final class CraftingTweaksIntegration {
    private static final String ID = "craftingtweaks";

    private CraftingTweaksIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ID);
    }

    public static boolean isCraftingTweaksClass(Class<?> clazz) {
        return clazz.getName().startsWith("net.blay09.mods.craftingtweaks");
    }

    public static void register() {
        CompoundNBT tag = new CompoundNBT();

        tag.putString("ContainerClass", GridContainer.class.getName());
        tag.putString("ValidContainerPredicate", ValidContainerPredicate.class.getName());
        tag.putString("GetGridStartFunction", GetGridStartFunction.class.getName());
        tag.putString("AlignToGrid", "left");

        InterModComms.sendTo(ID, "RegisterProvider", () -> tag);
    }

    public static class ValidContainerPredicate implements Predicate<GridContainer> {
        @Override
        public boolean test(GridContainer containerGrid) {
            return containerGrid.getGrid().getGridType() == GridType.CRAFTING;
        }
    }

    public static class GetGridStartFunction implements Function<GridContainer, Integer> {
        @Override
        public Integer apply(GridContainer containerGrid) {
            for (int i = 0; i < containerGrid.slots.size(); i++) {
                if (containerGrid.slots.get(i) instanceof CraftingGridSlot) {
                    return i;
                }
            }

            return 0;
        }
    }
}

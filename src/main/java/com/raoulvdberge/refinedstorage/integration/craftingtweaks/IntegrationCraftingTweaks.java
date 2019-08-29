package com.raoulvdberge.refinedstorage.integration.craftingtweaks;

/*
public final class IntegrationCraftingTweaks {
    private static final String ID = "craftingtweaks";

    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }

    public static void register() {
        CompoundNBT tag = new CompoundNBT();

        tag.setString("ContainerClass", ContainerGrid.class.getName());
        tag.setString("ValidContainerPredicate", ValidContainerPredicate.class.getName());
        tag.setString("GetGridStartFunction", GetGridStartFunction.class.getName());
        tag.setString("AlignToGrid", "left");

        FMLInterModComms.sendMessage(ID, "RegisterProviderV3", tag);
    }

    public static class ValidContainerPredicate implements Predicate<ContainerGrid> {
        @Override
        public boolean apply(ContainerGrid containerGrid) {
            return containerGrid.getGrid().getGridType() == GridType.CRAFTING;
        }
    }

    public static class GetGridStartFunction implements Function<ContainerGrid, Integer> {
        @Override
        public Integer apply(ContainerGrid containerGrid) {
            for (int i = 0; i < containerGrid.inventorySlots.size(); i++) {
                if (containerGrid.inventorySlots.get(i) instanceof SlotGridCrafting) {
                    return i;
                }
            }
            return 0;
        }
    }
}
*/

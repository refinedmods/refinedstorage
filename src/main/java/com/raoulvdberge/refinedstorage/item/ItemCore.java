package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;

public class ItemCore extends ItemBase {
    private static final int TYPE_CONSTRUCTION = 0;
    private static final int TYPE_DESTRUCTION = 1;

    public ItemCore() {
        super(new ItemInfo(RS.ID, "core"));

        /*setHasSubtypes(true);
        setMaxDamage(0);*/
    }
/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelVariants(this, new ResourceLocation(RS.ID, "construction_core"), new ResourceLocation(RS.ID, "destruction_core"));

        modelRegistration.setModel(this, TYPE_CONSTRUCTION, new ModelResourceLocation(RS.ID + ":construction_core", "inventory"));
        modelRegistration.setModel(this, TYPE_DESTRUCTION, new ModelResourceLocation(RS.ID + ":destruction_core", "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i < 2; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }*/
}

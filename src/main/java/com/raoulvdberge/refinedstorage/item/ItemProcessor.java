package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;

public class ItemProcessor extends ItemBase {
    public static final int TYPE_CUT_BASIC = 0;
    public static final int TYPE_CUT_IMPROVED = 1;
    public static final int TYPE_CUT_ADVANCED = 2;
    public static final int TYPE_BASIC = 3;
    public static final int TYPE_IMPROVED = 4;
    public static final int TYPE_ADVANCED = 5;

    public ItemProcessor() {
        super(new ItemInfo(RS.ID, "processor"));

        //setHasSubtypes(true);
        //setMaxDamage(0);
    }
/* TODO
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelVariants(
            this,
            new ResourceLocation(RS.ID, "raw_basic_processor"),
            new ResourceLocation(RS.ID, "raw_improved_processor"),
            new ResourceLocation(RS.ID, "raw_advanced_processor"),
            new ResourceLocation(RS.ID, "basic_processor"),
            new ResourceLocation(RS.ID, "improved_processor"),
            new ResourceLocation(RS.ID, "advanced_processor")
        );

        modelRegistration.setModel(this, TYPE_CUT_BASIC, new ModelResourceLocation(RS.ID + ":raw_basic_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_CUT_IMPROVED, new ModelResourceLocation(RS.ID + ":raw_improved_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_CUT_ADVANCED, new ModelResourceLocation(RS.ID + ":raw_advanced_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_BASIC, new ModelResourceLocation(RS.ID + ":basic_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_IMPROVED, new ModelResourceLocation(RS.ID + ":improved_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_ADVANCED, new ModelResourceLocation(RS.ID + ":advanced_processor", "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 5; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }*/
}

package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemProcessor extends ItemBase {
    public static final int TYPE_CUT_BASIC = 0;
    public static final int TYPE_CUT_IMPROVED = 1;
    public static final int TYPE_CUT_ADVANCED = 2;
    public static final int TYPE_BASIC = 3;
    public static final int TYPE_IMPROVED = 4;
    public static final int TYPE_ADVANCED = 5;
    public static final int TYPE_CUT_SILICON = 6;

    public ItemProcessor() {
        super(new ItemInfo(RS.ID, "processor"));

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelVariants(
            this,
            new ResourceLocation(RS.ID, "cut_basic_processor"),
            new ResourceLocation(RS.ID, "cut_improved_processor"),
            new ResourceLocation(RS.ID, "cut_advanced_processor"),
            new ResourceLocation(RS.ID, "basic_processor"),
            new ResourceLocation(RS.ID, "improved_processor"),
            new ResourceLocation(RS.ID, "advanced_processor"),
            new ResourceLocation(RS.ID, "cut_silicon")
        );

        modelRegistration.setModel(this, TYPE_CUT_BASIC, new ModelResourceLocation(RS.ID + ":cut_basic_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_CUT_IMPROVED, new ModelResourceLocation(RS.ID + ":cut_improved_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_CUT_ADVANCED, new ModelResourceLocation(RS.ID + ":cut_advanced_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_BASIC, new ModelResourceLocation(RS.ID + ":basic_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_IMPROVED, new ModelResourceLocation(RS.ID + ":improved_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_ADVANCED, new ModelResourceLocation(RS.ID + ":advanced_processor", "inventory"));
        modelRegistration.setModel(this, TYPE_CUT_SILICON, new ModelResourceLocation(RS.ID + ":cut_silicon", "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 6; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}

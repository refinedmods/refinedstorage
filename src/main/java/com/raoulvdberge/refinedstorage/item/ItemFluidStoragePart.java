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

public class ItemFluidStoragePart extends ItemBase {
    public static final int TYPE_64K = 0;
    public static final int TYPE_256K = 1;
    public static final int TYPE_1024K = 2;
    public static final int TYPE_4096K = 3;

    public ItemFluidStoragePart() {
        super(new ItemInfo(RS.ID, "fluid_storage_part"));

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelVariants(
            this,
            new ResourceLocation(RS.ID, "64k_fluid_storage_part"),
            new ResourceLocation(RS.ID, "256k_fluid_storage_part"),
            new ResourceLocation(RS.ID, "1024k_fluid_storage_part"),
            new ResourceLocation(RS.ID, "4096k_fluid_storage_part")
        );

        modelRegistration.setModel(this, TYPE_64K, new ModelResourceLocation(RS.ID + ":64k_fluid_storage_part", "inventory"));
        modelRegistration.setModel(this, TYPE_256K, new ModelResourceLocation(RS.ID + ":256k_fluid_storage_part", "inventory"));
        modelRegistration.setModel(this, TYPE_1024K, new ModelResourceLocation(RS.ID + ":1024k_fluid_storage_part", "inventory"));
        modelRegistration.setModel(this, TYPE_4096K, new ModelResourceLocation(RS.ID + ":4096k_fluid_storage_part", "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 3; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}

package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHollowCover extends ItemCover {
    public ItemHollowCover() {
        super(new ItemInfo(RS.ID, "hollow_cover"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.HOLLOW);
    }
}

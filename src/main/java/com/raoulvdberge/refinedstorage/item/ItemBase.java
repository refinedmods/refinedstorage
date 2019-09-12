package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.item.info.IItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemBase extends Item {
    protected final IItemInfo info;

    public ItemBase(IItemInfo info) {
        super(new Item.Properties());
        this.info = info;

        setRegistryName(info.getId());
        // TODO setCreativeTab(RS.INSTANCE.tab);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
    }

    @Override
    public String getTranslationKey() {
        return "item." + info.getId().toString();
    }
}

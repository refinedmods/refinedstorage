package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;

public class ItemCuttingTool extends ItemBase {
    public ItemCuttingTool() {
        super(new ItemInfo(RS.ID, "cutting_tool"));

        //setMaxDamage(50 - 1);
        //setMaxStackSize(1);
    }
/* TODO
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.setItemDamage(stack.getItemDamage() + 1);

        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getTranslationKey(); // Ignore damage
    }*/
}

package com.raoulvdberge.refinedstorage.item;

public class ItemHollowCover extends ItemCover {
    public ItemHollowCover() {
        super();
    }
/* TODO
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.HOLLOW);
    }*/
}

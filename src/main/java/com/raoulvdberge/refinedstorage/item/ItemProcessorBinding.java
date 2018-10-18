package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public class ItemProcessorBinding extends ItemBase {
    public ItemProcessorBinding() {
        super(new ItemInfo(RS.ID, "processor_binding"));
    }

    @Override
    public void registerModels(IModelRegistration modelRegistration) {
        super.registerModels(modelRegistration);

        modelRegistration.setModel(this, 0, new ModelResourceLocation(RS.ID + ":processor_binding", "inventory"));
    }
}

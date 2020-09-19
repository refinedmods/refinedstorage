package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataGenerators {

    @SubscribeEvent
    public void runDataGeneration(GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new BlockModelGenerator(event.getGenerator(), RS.ID, event.getExistingFileHelper()));
            event.getGenerator().addProvider(new ItemModelGenerator(event.getGenerator(), RS.ID, event.getExistingFileHelper()));
        }
    }
}

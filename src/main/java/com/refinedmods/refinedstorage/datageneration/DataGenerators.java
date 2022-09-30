package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DataGenerators {
    @SubscribeEvent
    public void runDataGeneration(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new BlockModelGenerator(event.getGenerator(), RS.ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new RecipeGenerator(event.getGenerator()));
        BlockTagGenerator blockTagGenerator = new BlockTagGenerator(
            event.getGenerator(),
            RS.ID,
            event.getExistingFileHelper());
        event.getGenerator().addProvider(event.includeServer(), blockTagGenerator);
        event.getGenerator().addProvider(event.includeServer(), new ItemTagGenerator(
            event.getGenerator(),
            blockTagGenerator,
            RS.ID,
            event.getExistingFileHelper())
        );
        event.getGenerator().addProvider(event.includeServer(), new BlockEntityTagGenerator(
            event.getGenerator(),
            RS.ID,
            event.getExistingFileHelper()
        ));
        event.getGenerator().addProvider(event.includeServer(), new LootTableGenerator(event.getGenerator()));
    }
}

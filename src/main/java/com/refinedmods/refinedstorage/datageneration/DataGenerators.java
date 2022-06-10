package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class DataGenerators {
    @SubscribeEvent
    public void runDataGeneration(GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new BlockModelGenerator(event.getGenerator(), RS.ID, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            event.getGenerator().addProvider(new RecipeGenerator(event.getGenerator()));
            BlockTagGenerator blockTagGenerator = new BlockTagGenerator(event.getGenerator(), RS.ID, event.getExistingFileHelper());
            event.getGenerator().addProvider(blockTagGenerator);
            event.getGenerator().addProvider(new ItemTagGenerator(
                event.getGenerator(),
                blockTagGenerator,
                RS.ID,
                event.getExistingFileHelper())
            );
            event.getGenerator().addProvider(new LootTableGenerator(event.getGenerator()));
        }
    }
}

package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import java.util.List;
import java.util.Set;

public class DataGenerators {
    @SubscribeEvent
    public void runDataGeneration(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new BlockModelGenerator(event.getGenerator().getPackOutput(), RS.ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(event.includeServer(), new RecipeGenerator(event.getGenerator().getPackOutput()));
        BlockTagGenerator blockTagGenerator = new BlockTagGenerator(
            event.getGenerator().getPackOutput(),
            event.getLookupProvider(),
            RS.ID,
            event.getExistingFileHelper()
        );
        final BlockTagsProvider blockTagsProvider = event.getGenerator().addProvider(event.includeServer(), blockTagGenerator);
        event.getGenerator().addProvider(event.includeServer(), new ItemTagGenerator(
            event.getGenerator().getPackOutput(),
            event.getLookupProvider(),
            blockTagsProvider.contentsGetter(),
            RS.ID,
            event.getExistingFileHelper()
        ));
        event.getGenerator().addProvider(event.includeServer(), new BlockEntityTagGenerator(
            event.getGenerator().getPackOutput(),
            RS.ID,
            event.getLookupProvider(),
            event.getExistingFileHelper()
        ));
        event.getGenerator().addProvider(event.includeServer(), new LootTableProvider(
            event.getGenerator().getPackOutput(),
            Set.of(),
            List.of(new LootTableProvider.SubProviderEntry(
                LootTableGenerator::new,
                LootContextParamSets.BLOCK
            ))
        ));
    }
}
